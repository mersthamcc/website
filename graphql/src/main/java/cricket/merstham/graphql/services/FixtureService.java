package cricket.merstham.graphql.services;

import cricket.merstham.graphql.dto.PlayCricketMatch;
import cricket.merstham.graphql.entity.FantasyPlayerStatisticEntity;
import cricket.merstham.graphql.entity.FantasyPlayerStatisticEntityId;
import cricket.merstham.graphql.entity.FixtureEntity;
import cricket.merstham.graphql.entity.FixturePlayerSummaryEntity;
import cricket.merstham.graphql.entity.FixturePlayerSummaryEntityId;
import cricket.merstham.graphql.entity.LastUpdateEntity;
import cricket.merstham.graphql.entity.LeagueEntity;
import cricket.merstham.graphql.entity.PlayerEntity;
import cricket.merstham.graphql.entity.TeamEntity;
import cricket.merstham.graphql.entity.VenueEntity;
import cricket.merstham.graphql.repository.FantasyPlayerStatisticRepository;
import cricket.merstham.graphql.repository.FixturePlayerSummaryEntityRepository;
import cricket.merstham.graphql.repository.FixtureRepository;
import cricket.merstham.graphql.repository.LastUpdateRepository;
import cricket.merstham.graphql.repository.LeagueRepository;
import cricket.merstham.graphql.repository.PlayerRepository;
import cricket.merstham.graphql.repository.TeamRepository;
import cricket.merstham.graphql.repository.VenueRepository;
import cricket.merstham.shared.dto.CalendarSyncResult;
import cricket.merstham.shared.dto.FantasyPlayerStatistic;
import cricket.merstham.shared.dto.Fixture;
import cricket.merstham.shared.dto.FixturePlayer;
import cricket.merstham.shared.dto.League;
import cricket.merstham.shared.dto.Team;
import cricket.merstham.shared.extensions.StringExtensions;
import io.micrometer.core.annotation.Timed;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import lombok.experimental.ExtensionMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cricket.merstham.graphql.configuration.CacheConfiguration.ACTIVE_TEAM_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.FIXTURES_WON_COUNT_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.FIXTURE_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.FIXTURE_COUNT_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.SELECTION_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.TEAM_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.TEAM_FIXTURE_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.UPCOMING_FIXTURE_CACHE;
import static cricket.merstham.graphql.helpers.SelectionHelper.getThisWeekendsDates;
import static cricket.merstham.graphql.services.PlayCricketService.HOME;
import static cricket.merstham.graphql.services.PlayCricketService.PLAYER_ID;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static software.amazon.awssdk.http.HttpStatusCode.NOT_FOUND;

@Service
@ExtensionMethod({StringExtensions.class})
public class FixtureService {

    private static final Logger LOG = LogManager.getLogger(FixtureService.class);
    private static final String OTHER = "Other";
    private static final String FIXTURES = "fixtures";
    private static final List<String> NOT_OUT =
            List.of("not out", "did not bat", "retired not out");
    private final PlayCricketService playCricketService;
    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final LastUpdateRepository lastUpdateRepository;
    private final FixtureRepository fixtureRepository;
    private final LeagueRepository leagueRepository;
    private final PlayerRepository playerRepository;
    private final FixturePlayerSummaryEntityRepository playerSummaryRepository;
    private final FantasyPlayerStatisticRepository fantasyPlayerStatisticRepository;
    private final GoogleCalendarService googleCalendarService;
    private final VenueRepository venueRepository;

    @Autowired
    public FixtureService(
            PlayCricketService playCricketService,
            ModelMapper modelMapper,
            TeamRepository teamRepository,
            LastUpdateRepository lastUpdateRepository,
            FixtureRepository fixtureRepository,
            LeagueRepository leagueRepository,
            PlayerRepository playerRepository,
            FixturePlayerSummaryEntityRepository playerSummaryRepository,
            FantasyPlayerStatisticRepository fantasyPlayerStatisticRepository,
            GoogleCalendarService googleCalendarService,
            VenueRepository venueRepository) {
        this.playCricketService = playCricketService;
        this.modelMapper = modelMapper;
        this.teamRepository = teamRepository;
        this.lastUpdateRepository = lastUpdateRepository;
        this.fixtureRepository = fixtureRepository;
        this.leagueRepository = leagueRepository;
        this.playerRepository = playerRepository;
        this.playerSummaryRepository = playerSummaryRepository;
        this.fantasyPlayerStatisticRepository = fantasyPlayerStatisticRepository;
        this.googleCalendarService = googleCalendarService;
        this.venueRepository = venueRepository;
    }

    @Cacheable(value = ACTIVE_TEAM_CACHE)
    public List<Team> getActiveTeams() {
        return teamRepository.findByStatusAllIgnoreCaseOrderBySortOrderAsc("active").stream()
                .filter(t -> !t.isHidden())
                .map(t -> modelMapper.map(t, Team.class))
                .toList();
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll(Sort.by(Sort.Direction.ASC, "sortOrder")).stream()
                .filter(t -> !t.isHidden())
                .map(t -> modelMapper.map(t, Team.class))
                .toList();
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
                .toList();
    }

    @Cacheable(value = TEAM_CACHE, key = "{#season, #teamId}")
    public List<Fixture> getFixtures(int season, int teamId) {
        var firstDayOfYear = LocalDate.of(season, 1, 1);
        var lastDayOfYear = LocalDate.of(season, 12, 31);
        var team = teamRepository.findById(teamId).orElseThrow();
        return fixtureRepository
                .findByTeamIsAndDateIsBetweenOrderByDateAscStartAsc(
                        team, firstDayOfYear, lastDayOfYear)
                .stream()
                .map(f -> modelMapper.map(f, Fixture.class).setFocusTeam(teamId))
                .toList();
    }

    @Cacheable(value = FIXTURE_CACHE, key = "#season")
    public List<Fixture> refreshFixtures(int season) {
        var fixtures = playCricketService.getFixtures(season);
        return saveFixtures(fixtures).stream().map(f -> modelMapper.map(f, Fixture.class)).toList();
    }

    @Cacheable(value = TEAM_FIXTURE_CACHE, key = "#id")
    public List<Fixture> getFixturesForTeam(int id) {
        var team = teamRepository.findById(id).orElseThrow();
        return fixtureRepository.findAllByTeam(team).stream()
                .map(f -> modelMapper.map(f, Fixture.class))
                .toList();
    }

    public List<League> getLeaguesForTeam(int season, int teamId) {
        var firstDayOfYear = LocalDate.of(season, 1, 1);
        var lastDayOfYear = LocalDate.of(season, 12, 31);
        var team = teamRepository.findById(teamId).orElseThrow();
        var leagues =
                fixtureRepository
                        .findByTeamIsAndDateIsBetweenOrderByDateAscStartAsc(
                                team, firstDayOfYear, lastDayOfYear)
                        .stream()
                        .map(f -> f.getDetail().at("/competition_id").asInt())
                        .distinct()
                        .filter(integer -> integer > 0)
                        .toList();

        return leagueRepository.findAllById(leagues).stream()
                .map(l -> modelMapper.map(l, League.class))
                .toList();
    }

    @Timed(
            value = "playcricket.players.refresh",
            description = "Time taken to process players from PlayCricket")
    public void refreshPlayers() {
        LOG.info("Starting PlayCricket player refresh...");
        try {
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

        } catch (Exception ex) {
            LOG.error("Error in PlayCricket player refresh", ex);
        }
        LOG.info("Finished PlayCricket player refresh!");
    }

    @Scheduled(
            cron = "${configuration.play-cricket.team-refresh-cron}",
            zone = "${configuration.scheduler-zone}")
    @Timed(
            value = "playcricket.teams.refresh",
            description = "Time taken to process teams from PlayCricket")
    @Caching(
            evict = {
                @CacheEvict(value = TEAM_CACHE, allEntries = true),
                @CacheEvict(value = ACTIVE_TEAM_CACHE, allEntries = true),
            })
    @Lazy
    public void refreshTeams() {
        LOG.info("Starting PlayCricket team refresh...");
        try {
            refreshPlayers();
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
        } catch (Exception ex) {
            LOG.error("Error in PlayCricket team refresh", ex);
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
            zone = "${configuration.scheduler-zone}")
    @Timed(
            value = "playcricket.fixtures.refresh",
            description = "Time taken to process fixtures from PlayCricket")
    @Caching(
            evict = {
                @CacheEvict(value = FIXTURE_CACHE, allEntries = true),
                @CacheEvict(value = TEAM_FIXTURE_CACHE, allEntries = true),
                @CacheEvict(value = TEAM_CACHE, allEntries = true),
                @CacheEvict(value = ACTIVE_TEAM_CACHE, allEntries = true),
                @CacheEvict(value = FIXTURE_COUNT_CACHE, allEntries = true),
                @CacheEvict(value = FIXTURES_WON_COUNT_CACHE, allEntries = true),
                @CacheEvict(value = UPCOMING_FIXTURE_CACHE, allEntries = true),
            })
    @Lazy
    public void refreshFixtures() {
        LOG.info("Starting PlayCricket fixture refresh... ");
        try {
            var now = Instant.now();
            var lastUpdateEntity = lastUpdateRepository.findById(FIXTURES);
            var lastUpdate = lastUpdateEntity.map(LastUpdateEntity::getLastUpdate);
            var fixtures = playCricketService.getFixtures(lastUpdate, now);

            saveFixtures(fixtures.stream().filter(f -> f.getStatus().equals("New")).toList());

            var deletedFixtures =
                    fixtures.stream().filter(f -> f.getStatus().equals("Deleted")).toList();

            deletedFixtures.forEach(
                    f -> {
                        var entity = fixtureRepository.findById(f.getId());
                        if (entity.isPresent() && nonNull(entity.get().getCalendarId())) {
                            try {
                                googleCalendarService.deleteFixtureEvent(entity.get());
                            } catch (GeneralSecurityException | IOException e) {
                                LOG.error(
                                        () ->
                                                format(
                                                        "Error deleting fixture {0} from Google Calendar~",
                                                        f.getId()),
                                        e);
                            }
                        }
                    });

            fixtureRepository.deleteAllByIdInBatch(
                    deletedFixtures.stream().map(PlayCricketMatch::getId).toList());

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
        } catch (Exception ex) {
            LOG.error("Error in PlayCricket fixture refresh", ex);
        }
        LOG.info("Finished PlayCricket fixture refresh!");
    }

    @Timed(
            value = "google.fixtures.refresh",
            description = "Time taken to sync fixtures with Google Calendar")
    @Scheduled(
            cron = "${configuration.google.google-calendar-sync-cron}",
            zone = "${configuration.scheduler-zone}")
    @Lazy
    public List<CalendarSyncResult> syncFixturesWithCalendar() {
        return syncFixturesWithCalendar(LocalDate.of(LocalDate.now().getYear(), 1, 1));
    }

    public List<CalendarSyncResult> syncFixturesWithCalendar(LocalDate start) {
        var results = new ArrayList<CalendarSyncResult>();
        LOG.info("Starting Google Calendar fixture refresh... ");
        try {
            var fixtures = fixtureRepository.findAllByDateAfterAndHomeAwayEquals(start, HOME);

            fixtures.forEach(
                    f -> {
                        try {
                            VenueEntity venue = null;
                            if (nonNull(f.getGroundId())) {
                                venue = venueRepository.findByPlayCricketId(f.getGroundId());
                            }
                            var calendarId = googleCalendarService.syncFixtureEvent(f, venue);
                            f.setCalendarId(calendarId);
                            results.add(
                                    CalendarSyncResult.builder()
                                            .fixtureId(f.getId())
                                            .calendarId(calendarId)
                                            .build());
                        } catch (GeneralSecurityException | IOException e) {
                            LOG.error("Error in Google Calendar fixture refresh", e);
                        }
                    });

            fixtureRepository.saveAll(fixtures);
        } catch (Exception ex) {
            LOG.error("Error in Google Calendar fixture refresh", ex);
        }
        LOG.info("Finished Google Calendar fixture refresh!");
        return results;
    }

    public FantasyPlayerStatistic getPlayerStatistics(FixturePlayer player, int season) {
        var id =
                FantasyPlayerStatisticEntityId.builder()
                        .id(player.getPlayerId())
                        .year(season)
                        .build();

        var result =
                fantasyPlayerStatisticRepository
                        .findDistinctByIdIs(id)
                        .orElseGet(FantasyPlayerStatisticEntity::new);

        return modelMapper.map(result, FantasyPlayerStatistic.class);
    }

    private List<FixtureEntity> saveFixtures(List<PlayCricketMatch> fixtures) {
        List<FixtureEntity> updates = new ArrayList<>();

        fixtures.forEach(
                fixture -> {
                    try {
                        LOG.info("Processing fixture {}", fixture);
                        var home = "HOME".equals(fixture.getHomeAway());
                        var team =
                                teamRepository.findById(
                                        home ? fixture.getHomeTeamId() : fixture.getAwayTeamId());
                        var additionalTeamId =
                                (Objects.equals(fixture.getHomeClubId(), fixture.getAwayClubId())
                                                && home)
                                        ? fixture.getAwayTeamId()
                                        : null;
                        if (team.isEmpty()) {
                            LOG.warn(
                                    "Team not found {} - {}",
                                    home ? fixture.getHomeTeamId() : fixture.getAwayTeamId(),
                                    home ? fixture.getHomeTeamName() : fixture.getAwayTeamName());
                        } else {
                            var entity =
                                    fixtureRepository
                                            .findById(fixture.getId())
                                            .orElseGet(
                                                    () ->
                                                            FixtureEntity.builder()
                                                                    .id(fixture.getId())
                                                                    .includeInFantasy(
                                                                            fixture.getMatchDate()
                                                                                            .getDayOfWeek()
                                                                                            .equals(
                                                                                                    DayOfWeek
                                                                                                            .SATURDAY)
                                                                                    && fixture.getCompetitionType()
                                                                                            .equals(
                                                                                                    "League")
                                                                                    && team.get()
                                                                                            .isIncludedInSelection())
                                                                    .build())
                                            .setDate(fixture.getMatchDate())
                                            .setStart(fixture.getMatchTime())
                                            .setOpposition(
                                                    home
                                                            ? fixture.getAwayClubName()
                                                            : fixture.getHomeClubName())
                                            .setHomeAway(fixture.getHomeAway())
                                            .setTeam(team.get())
                                            .setDetail(fixture.getDetails())
                                            .setGroundId(home ? fixture.getGroundId() : null)
                                            .setOppositionTeamId(additionalTeamId);

                            playerSummaryRepository.saveAllAndFlush(processPlayerStats(entity));
                            try {
                                VenueEntity venue = null;
                                if (nonNull(entity.getGroundId())) {
                                    venue =
                                            venueRepository.findByPlayCricketId(
                                                    entity.getGroundId());
                                }
                                entity.setCalendarId(
                                        googleCalendarService.syncFixtureEvent(entity, venue));
                            } catch (Exception ex) {
                                LOG.error("Error in Google Calendar fixture sync", ex);
                            }
                            updates.add(fixtureRepository.save(entity));
                        }
                    } catch (Exception ex) {
                        LOG.atError()
                                .withThrowable(ex)
                                .log("Error preparing fixture {} for update", fixture.getId());
                    }
                });

        if (updates.isEmpty()) {
            LOG.info("No fixture updates found!");
        } else {
            LOG.info("Saved {} updated fixtures to database ...", updates.size());
        }
        return updates;
    }

    private List<FixturePlayerSummaryEntity> processPlayerStats(FixtureEntity fixture) {
        List<FixturePlayerSummaryEntity> result = new ArrayList<>();

        var players = playCricketService.getPlayers(fixture);
        var playerEntities =
                playerRepository.findAllById(
                        players.stream().map(p -> p.get(PLAYER_ID).asInt()).toList());

        if (players.size() != playerEntities.size()) {
            LOG.atWarn()
                    .log(
                            "Players not found for fixture {}: {}",
                            fixture.getId(),
                            players.stream()
                                    .filter(
                                            player ->
                                                    playerEntities.stream()
                                                            .filter(
                                                                    p ->
                                                                            p.getId()
                                                                                    .equals(
                                                                                            player.get(
                                                                                                            PLAYER_ID)
                                                                                                    .asInt()))
                                                            .findFirst()
                                                            .isEmpty())
                                    .toList());
        }
        players.forEach(
                p -> {
                    var playerId = p.get(PLAYER_ID).asInt();
                    var batting = playCricketService.getBatting(fixture, playerId);
                    var bowling = playCricketService.getBowling(fixture, playerId);

                    var id =
                            FixturePlayerSummaryEntityId.builder()
                                    .fixtureId(fixture.getId())
                                    .playerId(playerId)
                                    .build();
                    var player =
                            playerEntities.stream()
                                    .filter(playerEntity -> playerEntity.getId().equals(playerId))
                                    .findFirst()
                                    .orElseGet(
                                            () ->
                                                    playerRepository.save(
                                                            PlayerEntity.builder()
                                                                    .id(playerId)
                                                                    .detail(
                                                                            playCricketService
                                                                                    .createPlayer(
                                                                                            p))
                                                                    .build()));

                    var entity =
                            playerSummaryRepository
                                    .findById(id)
                                    .orElseGet(
                                            () ->
                                                    FixturePlayerSummaryEntity.builder()
                                                            .id(
                                                                    FixturePlayerSummaryEntityId
                                                                            .builder()
                                                                            .build())
                                                            .player(player)
                                                            .fixture(fixture)
                                                            .build());

                    if (nonNull(batting)) {
                        entity.setRuns(batting.get("runs").asInt(0));
                        entity.setBalls(batting.get("balls").asInt(0));
                        entity.setOut(!NOT_OUT.contains(batting.get("how_out").asText()));
                        entity.setDnb(batting.get("how_out").asText().equals("did not bat"));
                        entity.setFours(batting.get("fours").asInt(0));
                        entity.setSixes(batting.get("sixes").asInt(0));
                        entity.setHowOut(batting.get("how_out").asText());
                    }

                    if (nonNull(bowling)) {
                        entity.setWickets(bowling.get("wickets").asInt(0));
                        entity.setOvers(BigDecimal.valueOf(bowling.get("overs").asDouble()));
                        entity.setMaidens(bowling.get("maidens").asInt(0));
                        entity.setConcededRuns(bowling.get("runs").asInt(0));
                    }
                    entity.setCatches(playCricketService.getCatches(fixture, playerId));

                    result.add(entity);
                });

        return result;
    }

    public List<Integer> getAllFixtureSeasons() {
        return fixtureRepository.findDistinctYears();
    }

    @Cacheable(value = SELECTION_CACHE)
    public List<Fixture> getThisWeeksSelection() {
        var dates = getThisWeekendsDates(LocalDate.now());
        var fixtures = fixtureRepository.findAllByDateInAndTeamIncludedInSelectionIsTrue(dates);
        var fixturesToDelete = new ArrayList<FixtureEntity>();

        fixtures.stream()
                .forEach(
                        fixture -> {
                            try {
                                fixture.setDetail(
                                        playCricketService.getMatchDetails(fixture.getId()));
                            } catch (WebApplicationException e) {
                                if (e.getResponse().getStatus() == NOT_FOUND) {
                                    LOG.atWarn()
                                            .withThrowable(e)
                                            .log(
                                                    "PlayCricket returned 404 for fixture {}, removing from local database.",
                                                    fixture.getId());

                                    if (fixture.getCalendarId() != null) {
                                        try {
                                            googleCalendarService.deleteFixtureEvent(fixture);
                                        } catch (GeneralSecurityException | IOException ex) {
                                            LOG.atError()
                                                    .withThrowable(ex)
                                                    .log(
                                                            "Error removing fixture from Google calendar {}",
                                                            fixture.getId());
                                        }
                                    }
                                    fixturesToDelete.add(fixture);
                                } else {
                                    LOG.atError()
                                            .withThrowable(e)
                                            .log(
                                                    "Error while getting match details for fixture {}",
                                                    fixture.getId());
                                }
                            }
                        });

        fixtures.removeAll(fixturesToDelete);
        fixtureRepository.saveAllAndFlush(fixtures);
        if (!fixturesToDelete.isEmpty()) {
            fixtureRepository.deleteAllByIdInBatch(
                    fixturesToDelete.stream()
                            .map(FixtureEntity::getId)
                            .collect(Collectors.toList()));
        }

        return fixtures.stream()
                .map(f -> modelMapper.map(f, Fixture.class))
                .sorted(
                        Comparator.comparing(Fixture::getDate)
                                .thenComparingLong(f -> f.getTeam().getSortOrder()))
                .toList();
    }

    @Cacheable(value = UPCOMING_FIXTURE_CACHE)
    public List<Fixture> getUpcomingFixtures(int count) {
        return fixtureRepository
                .findByDateAfterOrderByDateAscStartAsc(
                        LocalDate.now().minusDays(1), PageRequest.of(0, count))
                .stream()
                .map(f -> modelMapper.map(f, Fixture.class))
                .toList();
    }

    @Cacheable(value = FIXTURE_COUNT_CACHE)
    public long getFixtureCount() {
        var year = LocalDate.now().getYear();
        return fixtureRepository.countByDateBetween(
                LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
    }

    @Cacheable(value = FIXTURES_WON_COUNT_CACHE)
    public long getFixtureWinCount() {
        var year = LocalDate.now().getYear();
        return fixtureRepository.countWinsByDateBetween(
                LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
    }
}
