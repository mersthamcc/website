package cricket.merstham.graphql.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Strings;
import cricket.merstham.graphql.entity.FixtureEntity;
import cricket.merstham.graphql.entity.VenueEntity;
import jakarta.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static cricket.merstham.graphql.services.PlayCricketService.AWAY;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

@Service
public class GoogleCalendarService {
    private static final Logger LOG = LogManager.getLogger(GoogleCalendarService.class);

    private final GoogleCredentials googleCredentials;
    private final String applicationName;
    private final String calendarId;
    private final String zoneId;

    @Autowired
    public GoogleCalendarService(
            @Named("CalendarCredentials") GoogleCredentials googleCredentials,
            @Value("${configuration.google.application-name}") String applicationName,
            @Value("${configuration.google.club-calendar-id}") String calendarId,
            @Value("${configuration.scheduler-zone}") String zoneId) {
        this.googleCredentials = googleCredentials;
        this.applicationName = applicationName;
        this.calendarId = calendarId;
        this.zoneId = zoneId;
    }

    public String syncFixtureEvent(FixtureEntity fixture, VenueEntity venue)
            throws GeneralSecurityException, IOException {
        LOG.info("Initiating sync of fixture {} with Google Calendar", fixture.getId());

        if (AWAY.equals(fixture.getHomeAway())) {
            deleteFixtureEvent(fixture);
            return null;
        }

        var service =
                new Calendar.Builder(
                                GoogleNetHttpTransport.newTrustedTransport(),
                                GsonFactory.getDefaultInstance(),
                                new HttpCredentialsAdapter(googleCredentials))
                        .setApplicationName(applicationName)
                        .build();

        var event = fixtureToEvent(fixture, venue);

        if (isNull(fixture.getCalendarId())) {
            event = service.events().insert(calendarId, event).execute();
        } else {
            event = service.events().update(calendarId, fixture.getCalendarId(), event).execute();
        }
        return event.getId();
    }

    public void deleteFixtureEvent(FixtureEntity fixture)
            throws GeneralSecurityException, IOException {
        LOG.info("Deleting fixture {} from Google Calendar", fixture.getId());
        var service =
                new Calendar.Builder(
                                GoogleNetHttpTransport.newTrustedTransport(),
                                GsonFactory.getDefaultInstance(),
                                new HttpCredentialsAdapter(googleCredentials))
                        .setApplicationName(applicationName)
                        .build();

        service.events().delete(calendarId, fixture.getCalendarId()).execute();
    }

    private Event fixtureToEvent(FixtureEntity fixture, VenueEntity venue) {
        var event = new Event();
        event.setSummary(
                format(
                        "[{0}] {1} vs {2}",
                        isNull(venue) ? "unknown" : venue.getSlug().toUpperCase(),
                        fixture.getTeam().getName(),
                        fixture.getOpposition()));
        var start = constructStartDateTime(fixture);
        event.setStart(new EventDateTime().setDateTime(start));
        event.setEnd(new EventDateTime().setDateTime(constructEndDateTime(fixture)));
        event.setLocation(
                isNull(venue) ? "unknown" : venue.getMarker() + ", " + venue.getPostCode());
        event.setDescription(
                format(
                        "Fixture Link: https://www.play-cricket.com/match_details?id={0,number,#}\n"
                                + "Ground: {1}",
                        fixture.getId(),
                        isNull(venue)
                                ? "Ground not specified or invalid in Play-Cricket"
                                : venue.getName()));

        return event;
    }

    private DateTime constructStartDateTime(FixtureEntity fixture) {
        ZonedDateTime start =
                ZonedDateTime.of(fixture.getDate(), fixture.getStart(), ZoneId.of(zoneId));
        return DateTime.parseRfc3339(start.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    private DateTime constructEndDateTime(FixtureEntity fixture) {
        ZonedDateTime start =
                ZonedDateTime.of(fixture.getDate(), fixture.getStart(), ZoneId.of(zoneId));
        ZonedDateTime end = start.plus(fixtureLength(fixture), ChronoUnit.HOURS);
        return DateTime.parseRfc3339(end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    private long fixtureLength(FixtureEntity fixture) {
        var oversAsString = fixture.getDetail().at("/no_of_overs").asText("20");
        if (Strings.isNullOrEmpty(oversAsString)) {
            return 2;
        }
        int overs = Integer.parseInt(oversAsString);
        if (overs < 20) {
            return 2;
        }
        if (overs == 20) {
            return 3;
        }
        return 6;
    }
}
