package cricket.merstham.graphql.services;

import cricket.merstham.graphql.dto.PlayCricketMatch;
import cricket.merstham.graphql.entity.FixtureEntity;
import cricket.merstham.graphql.entity.LastUpdateEntity;
import cricket.merstham.graphql.entity.LeagueEntity;
import cricket.merstham.graphql.entity.PlayerEntity;
import cricket.merstham.graphql.entity.TeamEntity;
import cricket.merstham.graphql.repository.FixtureRepository;
import cricket.merstham.graphql.repository.LastUpdateRepository;
import cricket.merstham.graphql.repository.LeagueRepository;
import cricket.merstham.graphql.repository.PlayerRepository;
import cricket.merstham.graphql.repository.TeamRepository;
import cricket.merstham.shared.dto.Fixture;
import cricket.merstham.shared.dto.League;
import cricket.merstham.shared.dto.Team;
import cricket.merstham.shared.extensions.StringExtensions;
import io.micrometer.core.annotation.Timed;
import jakarta.ws.rs.NotFoundException;
import lombok.experimental.ExtensionMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cricket.merstham.graphql.configuration.CacheConfiguration.ACTIVE_TEAM_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.FIXTURE_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.TEAM_CACHE;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

@Service
@ExtensionMethod({StringExtensions.class})
public class FixtureService {

    private static final Logger LOG = LogManager.getLogger(FixtureService.class);
    private static final String OTHER = "Other";
    private static final String FIXTURES = "fixtures";
    private final PlayCricketService playCricketService;
    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final LastUpdateRepository lastUpdateRepository;
    private final FixtureRepository fixtureRepository;
    private final LeagueRepository leagueRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public FixtureService(
            PlayCricketService playCricketService,
            ModelMapper modelMapper,
            TeamRepository teamRepository,
            LastUpdateRepository lastUpdateRepository,
            FixtureRepository fixtureRepository,
            LeagueRepository leagueRepository,
            PlayerRepository playerRepository) {
        this.playCricketService = playCricketService;
        this.modelMapper = modelMapper;
        this.teamRepository = teamRepository;
        this.lastUpdateRepository = lastUpdateRepository;
        this.fixtureRepository = fixtureRepository;
        this.leagueRepository = leagueRepository;
        this.playerRepository = playerRepository;
    }

    @Cacheable(value = ACTIVE_TEAM_CACHE)
    public List<Team> getActiveTeams() {
        return teamRepository.findByStatusAllIgnoreCaseOrderBySortOrderAsc("active").stream()
                .filter(t -> !t.isHidden())
                .map(t -> modelMapper.map(t, Team.class))
                .collect(Collectors.toList());
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder")).stream()
                .filter(t -> !t.isHidden())
                .map(t -> modelMapper.map(t, Team.class))
                .collect(Collectors.toList());
    }

    public Team getTeam(int id) {
        return teamRepository.findById(id).map(t -> modelMapper.map(t, Team.class)).orElseThrow();
    }

    public List<Fixture> getFixtures(int season) {
        var firstDayOfYear = LocalDate.of(season, 1, 1);
        var lastDayOfYear = LocalDate.of(season, 12, 31);
        return fixtureRepository
                .findByDateIsBetweenOrderByDateAscStartAsc(firstDayOfYear, lastDayOfYear)
                .stream()
                .map(f -> modelMapper.map(f, Fixture.class))
                .collect(Collectors.toList());
    }

    @Cacheable(value = TEAM_CACHE, key = "{#season, #teamId}")
    public List<Fixture> getFixtures(int season, int teamId) {
        var firstDayOfYear = LocalDate.of(season, 1, 1);
        var lastDayOfYear = LocalDate.of(season, 12, 31);
        var team = teamRepository.findById(teamId).orElseThrow();
        return fixtureRepository
                .findByTeamIdAndDateIsBetweenOrderByDateAscStartAsc(
                        team, firstDayOfYear, lastDayOfYear)
                .stream()
                .map(f -> modelMapper.map(f, Fixture.class).setFocusTeam(teamId))
                .collect(Collectors.toList());
    }

    @Cacheable(value = FIXTURE_CACHE, key = "#season")
    public List<Fixture> refreshFixtures(int season) {
        var fixtures = playCricketService.getFixtures(season);
        return saveFixtures(fixtures).stream()
                .map(f -> modelMapper.map(f, Fixture.class))
                .collect(Collectors.toList());
    }

    public List<League> getLeaguesForTeam(int season, int teamId) {
        var firstDayOfYear = LocalDate.of(season, 1, 1);
        var lastDayOfYear = LocalDate.of(season, 12, 31);
        var team = teamRepository.findById(teamId).orElseThrow();
        var leagues =
                fixtureRepository
                        .findByTeamIdAndDateIsBetweenOrderByDateAscStartAsc(
                                team, firstDayOfYear, lastDayOfYear)
                        .stream()
                        .map(f -> f.getDetail().at("/competition_id").asInt())
                        .distinct()
                        .filter(integer -> integer > 0)
                        .collect(Collectors.toList());

        return leagueRepository.findAllById(leagues).stream()
                .map(l -> modelMapper.map(l, League.class))
                .collect(Collectors.toList());
    }

    @Timed(
            value = "playcricket.players.refresh",
            description = "Time taken to process players from PlayCricket")
    public void refreshPlayers() {
        LOG.info("Starting PlayCricket player refresh...");
        var players = playCricketService.getPlayers();

        var updates = new ArrayList<PlayerEntity>();
        players.entrySet()
                .forEach(
                        p -> {
                            var entity =
                                    playerRepository
                                            .findById(p.getKey())
                                            .orElseGet(
                                                    () ->
                                                            PlayerEntity.builder()
                                                                    .id(p.getKey())
                                                                    .build())
                                            .setDetail(p.getValue());

                            updates.add(entity);
                        });

        if (!updates.isEmpty()) {
            LOG.info("Saving PlayCricket player data.");
            playerRepository.saveAllAndFlush(updates);
        }
        LOG.info("Finished PlayCricket player refresh!");
    }

    @Scheduled(
            cron = "${configuration.play-cricket.team-refresh-cron}",
            zone = "${configuration.play-cricket.scheduler-zone}")
    @Timed(
            value = "playcricket.teams.refresh",
            description = "Time taken to process teams from PlayCricket")
    public void refreshTeams() {
        refreshPlayers();
        LOG.info("Starting PlayCricket team refresh...");
        var teams = playCricketService.getTeams();

        int order = 1;
        for (var team : teams) {
            LOG.info("Processing team {}", team);
            var name =
                    Objects.equals(team.getTeamName(), OTHER)
                            ? team.getOtherTeamName()
                            : team.getTeamName();
            var entity =
                    teamRepository
                            .findById(team.getId())
                            .orElseGet(() -> TeamEntity.builder().id(team.getId()).build())
                            .setSortOrder(order)
                            .setSlug(name.toSlug())
                            .setName(name)
                            .setStatus(team.getStatus())
                            .setCaptain(getCaptain(team.getTeamCaptain()));
            try {
                teamRepository.saveAndFlush(entity);
            } catch (Exception e) {
                LOG.error(() -> format("Error saving team {0}", team.getId()), e);
            }
            order++;
        }
        LOG.info("Finished PlayCricket team refresh!");
    }

    private PlayerEntity getCaptain(String teamCaptain) {
        return isNull(teamCaptain) || teamCaptain.isBlank()
                ? null
                : playerRepository.findById(Integer.valueOf(teamCaptain)).orElse(null);
    }

    @Scheduled(
            cron = "${configuration.play-cricket.fixture-refresh-cron}",
            zone = "${configuration.play-cricket.scheduler-zone}")
    @Timed(
            value = "playcricket.fixtures.refresh",
            description = "Time taken to process fixtures from PlayCricket")
    public void refreshFixtures() {
        LOG.info("Starting PlayCricket fixture refresh... ");
        var now = Instant.now();
        var lastUpdateEntity = lastUpdateRepository.findById(FIXTURES);
        var lastUpdate = lastUpdateEntity.map(LastUpdateEntity::getLastUpdate);
        var fixtures = playCricketService.getFixtures(lastUpdate, now);

        saveFixtures(fixtures);
        lastUpdateRepository.saveAndFlush(
                lastUpdateEntity
                        .orElseGet(() -> LastUpdateEntity.builder().key(FIXTURES).build())
                        .setLastUpdate(now));

        LOG.info("Updating league info...");
        var leagueIds =
                fixtureRepository.findLeagueIdsForTeamsBetween(
                        LocalDate.of(LocalDate.now().getYear(), 1, 1),
                        LocalDate.of(LocalDate.now().getYear(), 12, 31));

        List<LeagueEntity> leagueUpdates = new ArrayList<>();
        for (var leagueId : leagueIds) {
            if (isNull(leagueId)) continue;
            LOG.info("Getting league details for id {}", leagueId);
            try {
                var league = playCricketService.getLeague(leagueId);
                leagueUpdates.add(
                        leagueRepository
                                .findById(leagueId)
                                .orElseGet(() -> LeagueEntity.builder().id(leagueId).build())
                                .setName(league.getName())
                                .setLastUpdate(now)
                                .setTable(league.getLeague().get(0)));
            } catch (NotFoundException e) {
                LOG.warn("League not found {}", leagueId);
            }
        }
        leagueRepository.saveAllAndFlush(leagueUpdates);
        LOG.info("Finished PlayCricket fixture refresh!");
    }

    private List<FixtureEntity> saveFixtures(List<PlayCricketMatch> fixtures) {
        List<FixtureEntity> updates = new ArrayList<>();

        fixtures.forEach(
                fixture -> {
                    LOG.info("Processing fixture {}", fixture);
                    var home = fixture.getHomeAway().equals("HOME");
                    var team =
                            teamRepository.findById(
                                    home ? fixture.getHomeTeamId() : fixture.getAwayTeamId());
                    var additionalTeamId =
                            (Objects.equals(fixture.getHomeClubId(), fixture.getAwayClubId())
                                            && home)
                                    ? fixture.getAwayTeamId()
                                    : null;
                    var entity =
                            fixtureRepository
                                    .findById(fixture.getId())
                                    .orElseGet(
                                            () ->
                                                    FixtureEntity.builder()
                                                            .id(fixture.getId())
                                                            .build())
                                    .setDate(fixture.getMatchDate())
                                    .setStart(fixture.getMatchTime())
                                    .setOpposition(
                                            home
                                                    ? fixture.getAwayClubName()
                                                    : fixture.getHomeClubName())
                                    .setHomeAway(fixture.getHomeAway())
                                    .setTeamId(team.orElseThrow())
                                    .setDetail(fixture.getDetails())
                                    .setGroundId(home ? fixture.getGroundId() : null)
                                    .setOppositionTeamId(additionalTeamId);

                    updates.add(entity);
                });

        if (updates.isEmpty()) {
            LOG.info("No fixture updates found!");
            return List.of();
        } else {
            LOG.info("Saving {} updates to database ...", updates.size());
            return fixtureRepository.saveAllAndFlush(updates);
        }
    }
}
