package cricket.merstham.graphql.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Video;
import com.google.common.base.Strings;
import cricket.merstham.graphql.dto.signage.Arguments;
import cricket.merstham.graphql.dto.signage.Event;
import cricket.merstham.graphql.dto.signage.MediaCreateRequest;
import cricket.merstham.graphql.dto.signage.MediaOrigin;
import cricket.merstham.graphql.dto.signage.PushToScreensRequest;
import cricket.merstham.graphql.dto.signage.ScheduleRequest;
import cricket.merstham.graphql.dto.signage.Source;
import cricket.merstham.graphql.entity.FixtureEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

@Service
public class SignageService {
    private static final Logger LOG = LogManager.getLogger(SignageService.class);

    private final RestTemplate restTemplate;
    private final String apiToken;
    private final int workspaceId;
    private final int folderId;
    private final int scheduleId;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public SignageService(
            @Value("${configuration.live-stream.yodeck-api-url}") String baseUrl,
            @Value("${configuration.live-stream.yodeck-api-token}") String apiToken,
            @Value("${configuration.live-stream.yodeck-workspace-id}") int workspaceId,
            @Value("${configuration.live-stream.yodeck-folder-id}") int folderId,
            @Value("${configuration.live-stream.yodeck-schedule-id}") int scheduleId,
            ObjectMapper objectMapper) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
        this.workspaceId = workspaceId;
        this.folderId = folderId;
        this.scheduleId = scheduleId;
        restTemplate = new RestTemplate();
        restTemplate
                .getInterceptors()
                .add(
                        (request, body, execution) -> {
                            request.getHeaders().set("Authorization", "Token " + apiToken);
                            return execution.execute(request, body);
                        });
        restTemplate.setRequestFactory(new JdkClientHttpRequestFactory());
        this.objectMapper = objectMapper;
    }

    public String createVideo(Video video) {
        try {
            var request =
                    MediaCreateRequest.builder()
                            .name(video.getSnippet().getTitle())
                            .description(video.getSnippet().getDescription())
                            .workspace(workspaceId)
                            .parentFolder(folderId)
                            .mediaOrigin(
                                    MediaOrigin.builder().type("video").source("stream").build())
                            .arguments(
                                    Arguments.builder()
                                            .playFromUrl(
                                                    format(
                                                            "https://www.youtube.com/watch?v={0}",
                                                            video.getId()))
                                            .build())
                            .tags(List.of())
                            .build();
            ResponseEntity<String> response =
                    restTemplate.exchange(
                            baseUrl + "/media",
                            HttpMethod.POST,
                            new HttpEntity<>(request),
                            String.class);
            JsonNode responseBody = objectMapper.readTree(response.getBody());

            return responseBody.get("id").asText();
        } catch (JsonProcessingException e) {
            LOG.error("Error creating Signage media", e);
            return null;
        }
    }

    public String createSchedule(Video video, String signageId, FixtureEntity fixture) {
        try {
            LOG.info("Creating new signage scheduled event");
            var events = getCurrentEvents();
            var existingIds = events.stream().map(Event::id).toList();
            var event =
                    Event.builder()
                            .start(
                                    convertToLocalDateTime(
                                            video.getLiveStreamingDetails()
                                                    .getScheduledStartTime()))
                            .duration(fixtureLengthInMinutes(fixture))
                            .recurrence("o")
                            .priority(10)
                            .source(
                                    Source.builder()
                                            .sourceType("media")
                                            .sourceId(Integer.parseInt(signageId))
                                            .build())
                            .build();
            events.add(event);
            var responseBody = saveScheduleEvents(events);
            var id =
                    convertToEvents(responseBody.get("events")).stream()
                            .filter(e -> !existingIds.contains(e.id()))
                            .findFirst()
                            .map(e -> Integer.toString(e.id()))
                            .orElseThrow();
            LOG.info("Created signage scheduled event with id {}", id);
            return id;
        } catch (Exception e) {
            LOG.error("Error creating Signage media", e);
            return null;
        }
    }

    public String updateSchedule(
            Video video, String signageId, FixtureEntity fixture, String eventId) {
        try {
            LOG.info("Updating existing signage scheduled event {}", eventId);
            var events = getCurrentEvents();
            var event =
                    events.stream()
                            .filter(e -> e.id().equals(Integer.parseInt(eventId)))
                            .findFirst()
                            .orElseThrow();
            event.start(
                            convertToLocalDateTime(
                                    video.getLiveStreamingDetails().getScheduledStartTime()))
                    .duration(fixtureLengthInMinutes(fixture))
                    .recurrence("o")
                    .priority(10)
                    .source(
                            Source.builder()
                                    .sourceType("media")
                                    .sourceId(Integer.parseInt(signageId))
                                    .build());
            saveScheduleEvents(events);
            LOG.info("Successfully updated existing signage scheduled event {}", eventId);
            return eventId;
        } catch (Exception e) {
            LOG.error("Error creating Signage media", e);
            return null;
        }
    }

    private JsonNode saveScheduleEvents(List<Event> events) throws JsonProcessingException {
        var request = ScheduleRequest.builder().events(events).build();
        ResponseEntity<String> response =
                restTemplate.exchange(
                        baseUrl + "/schedules/" + scheduleId,
                        HttpMethod.PATCH,
                        new HttpEntity<>(request),
                        String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return objectMapper.readTree(response.getBody());
        }
        LOG.error("Updating of signage schedule failed status error {}", response.getStatusCode());
        throw new RuntimeException("Signage schedule update failed");
    }

    private List<Event> getCurrentEvents() {
        var result =
                restTemplate.getForEntity(baseUrl + "/schedules/" + scheduleId, JsonNode.class);
        var events = result.getBody().get("events");
        return convertToEvents(events);
    }

    private List<Event> convertToEvents(JsonNode events) {
        try {
            return objectMapper.readerFor(new TypeReference<List<Event>>() {}).readValue(events);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String convertToLocalDateTime(DateTime scheduledStartTime) {
        var dt = Instant.parse(scheduledStartTime.toStringRfc3339()).minus(5, ChronoUnit.MINUTES);

        return dt.atZone(ZoneId.of("Europe/London")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private int fixtureLengthInMinutes(FixtureEntity fixture) {
        if (isNull(fixture)) return 420;
        var oversAsString = fixture.getDetail().at("/no_of_overs").asText("20");
        if (Strings.isNullOrEmpty(oversAsString)) {
            return 420;
        }
        int overs = Integer.parseInt(oversAsString);
        if (overs <= 20) {
            return 180;
        }
        return 420;
    }

    public void push() {
        LOG.info("Pushing signage updates to screens");
        var request =
                PushToScreensRequest.builder()
                        .useDownloadTimeslots(false)
                        .filterWorkspaces(List.of(workspaceId))
                        .build();
        var response =
                restTemplate.exchange(
                        baseUrl + "/screens/push",
                        HttpMethod.POST,
                        new HttpEntity<>(request),
                        String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            LOG.info("Push succeeded");
        } else {
            LOG.error("Push failed with status {}", response.getStatusCode().value());
        }
    }
}
