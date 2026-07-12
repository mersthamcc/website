package cricket.merstham.graphql.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import cricket.merstham.graphql.entity.FixtureEntity;
import cricket.merstham.graphql.entity.LiveStreamEntity;
import cricket.merstham.graphql.repository.FixtureRepository;
import cricket.merstham.graphql.repository.LiveStreamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static cricket.merstham.graphql.services.PlayCricketService.HOME;
import static java.util.Objects.isNull;

@Service
public class YouTubeService {

    private static final Logger LOG = LoggerFactory.getLogger(YouTubeService.class);

    private final String applicationName;
    private final TokenService tokenService;
    private final LiveStreamRepository liveStreamRepository;
    private final String channelId;
    private final FixtureRepository fixtureRepository;

    @Autowired
    public YouTubeService(
            @Value("${configuration.google.application-name}") String applicationName,
            TokenService tokenService,
            LiveStreamRepository liveStreamRepository,
            @Value("${configuration.live-stream.youtube-channel-id}") String channelId,
            FixtureRepository fixtureRepository) {
        this.tokenService = tokenService;
        this.applicationName = applicationName;
        this.liveStreamRepository = liveStreamRepository;
        this.channelId = channelId;
        this.fixtureRepository = fixtureRepository;
    }

    @Scheduled(
            cron = "${configuration.live-stream.sync-cron}",
            zone = "${configuration.scheduler-zone}")
    public void getLiveStreams() {
        LOG.info("*** Start of live stream sync ***");
        try {
            var token = tokenService.getToken("youtube");
            var credentials =
                    GoogleCredentials.create(AccessToken.newBuilder().setTokenValue(token).build());

            var service =
                    new YouTube.Builder(
                                    GoogleNetHttpTransport.newTrustedTransport(),
                                    GsonFactory.getDefaultInstance(),
                                    new HttpCredentialsAdapter(credentials))
                            .setApplicationName(applicationName)
                            .build();
            var request = service.search().list(List.of("snippet"));
            var response =
                    request.setChannelId(channelId)
                            .setEventType("upcoming")
                            .setMaxResults(50L)
                            .setOrder("date")
                            .setType(List.of("video"))
                            .execute();

            LOG.info("Found {} upcoming live streams", response.getItems().size());
            var ids = response.getItems().stream().map(item -> item.getId().getVideoId()).toList();
            var detailRequest =
                    service.videos()
                            .list(
                                    List.of(
                                            "snippet",
                                            "liveStreamingDetails",
                                            "contentDetails",
                                            "statistics",
                                            "player"));
            var details = detailRequest.setId(ids).execute();

            details.getItems()
                    .forEach(
                            video -> {
                                LOG.info(
                                        "Found stream: {} - {} due to start at {}",
                                        video.getId(),
                                        video.getSnippet().getTitle(),
                                        video.getLiveStreamingDetails().getScheduledStartTime());

                                var frogboxId = getFrogboxTag(video.getSnippet().getTags());
                                if (frogboxId.isPresent()) {
                                    var entity =
                                            liveStreamRepository
                                                    .getLiveStreamEntityByYoutubeId(video.getId())
                                                    .orElseGet(
                                                            () ->
                                                                    LiveStreamEntity.builder()
                                                                            .youtubeId(
                                                                                    video.getId())
                                                                            .build());
                                    entity.setTitle(video.getSnippet().getTitle())
                                            .setDescription(video.getSnippet().getDescription())
                                            .setStartTime(
                                                    convertToInstant(
                                                            video.getLiveStreamingDetails()
                                                                    .getScheduledStartTime()))
                                            .setEndTime(
                                                    convertToInstant(
                                                            video.getLiveStreamingDetails()
                                                                    .getScheduledEndTime()))
                                            .setFrogboxId(frogboxId.get())
                                            .setFixture(matchFixture(video))
                                            .setThumbnailUrl(
                                                    video.getSnippet()
                                                            .getThumbnails()
                                                            .getMedium()
                                                            .getUrl())
                                            .setWidget(video.getPlayer().getEmbedHtml());

                                    liveStreamRepository.save(entity);
                                } else {
                                    LOG.warn("Video {} is not a FrogBox stream", video.getId());
                                }
                            });
        } catch (IOException | GeneralSecurityException e) {
            LOG.error("Error getting YouTube data", e);
        }
        LOG.info("*** End of live stream sync ***");
    }

    private FixtureEntity matchFixture(Video video) {
        var start = convertToInstant(video.getLiveStreamingDetails().getScheduledStartTime());
        var title = video.getSnippet().getTitle();

        var fixture =
                fixtureRepository
                        .findAllByDateIn(
                                List.of(LocalDate.ofInstant(start, ZoneId.systemDefault())))
                        .stream()
                        .filter(
                                f ->
                                        title.contains(f.getTeam().getName())
                                                && title.contains(f.getOpposition())
                                                && f.getHomeAway().equals(HOME))
                        .findFirst();
        return fixture.orElse(null);
    }

    private Optional<String> getFrogboxTag(List<String> tags) {
        return tags.stream()
                .filter(tag -> tag.startsWith("FBID: "))
                .map(s -> s.substring(6))
                .findFirst();
    }

    private Instant convertToInstant(DateTime dateTime) {
        if (isNull(dateTime)) return null;
        return Instant.parse(dateTime.toStringRfc3339());
    }
}
