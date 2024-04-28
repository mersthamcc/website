package cricket.merstham.website.frontend.views;

import biweekly.component.VEvent;
import cricket.merstham.shared.dto.Fixture;
import cricket.merstham.website.frontend.service.FixtureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

@Component
public class FixtureCalendarFeedView extends CalendarFeedView<Fixture> {

    private final FixtureService service;

    @Autowired
    public FixtureCalendarFeedView(FixtureService service) {
        this.service = service;
    }

    @Override
    protected List<Fixture> getEvents(Map<String, Object> model) {
        try {
            var results = service.allFixturesForTeam((int) model.get("teamId"));
            var team = service.getTeam((int) model.get("teamId"));
            model.put("teamName", team.getName());
            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected VEvent mapEvent(Fixture entry, Map<String, Object> model) {
        var event = new VEvent();
        event.setDateStart(
                new Date(entry.getDate().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC) * 1000),
                false);
        event.setDescription(
                format(
                        "{0} vs {1}\nVenue: {2}\nStart: {3}",
                        entry.getTeam().getName(),
                        entry.getOpposition(),
                        entry.getVenue(),
                        entry.getStartTime()));
        event.setLocation(entry.getHomeAway());
        event.setSummary(
                format(
                        "[{0}] {1} vs {2}",
                        entry.getHomeAway(), entry.getTeam().getName(), entry.getOpposition()));
        event.addCategories(
                List.of(
                        entry.getTeam().getName(),
                        entry.getVenue(),
                        entry.isFriendly() ? "Friendly" : "League"));
        event.setUid(Integer.toHexString(entry.getId()));
        return event;
    }

    @Override
    protected String getCalendarName(Map<String, Object> model) {
        return format("{0} Fixtures Calendar", model.get("teamName"));
    }

    @Override
    protected String getCalendarDescription(Map<String, Object> model) {
        return format("{0} Fixtures Calendar", model.get("teamName"));
    }

    @Override
    protected String getTimeZone(Map<String, Object> model) {
        return "Europe/London";
    }
}
