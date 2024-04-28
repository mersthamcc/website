package cricket.merstham.website.frontend.views;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.CalendarScale;
import biweekly.property.Method;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.view.AbstractView;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public abstract class CalendarFeedView<T> extends AbstractView {
    @Override
    protected void renderMergedOutputModel(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<T> events = getEvents(model);
        response.addHeader("Content-Type", "text/calendar");

        ICalendar calendar = new ICalendar();
        calendar.setMethod(Method.publish());
        calendar.setCalendarScale(CalendarScale.gregorian());
        calendar.setName(getCalendarName(model));
        calendar.setProductId(getProductId(model));
        calendar.setDescription(getCalendarDescription(model));
        calendar.setExperimentalProperty("X-PUBLISHED-TTL", getTTL(model));
        calendar.setExperimentalProperty("X-WR-CALNAME", getCalendarName(model));
        calendar.setExperimentalProperty("X-WR-CALDESC", getCalendarDescription(model));
        calendar.setExperimentalProperty("X-WR-TIMEZONE", getTimeZone(model));

        events.stream().map(t -> mapEvent(t, model)).forEach(calendar::addEvent);

        OutputStream out = response.getOutputStream();
        Biweekly.write(calendar).go(out);
        out.flush();
    }

    protected String getTTL(Map<String, Object> model) {
        return "PT1H";
    }

    protected String getProductId(Map<String, Object> model) {
        return "Website Calendar Feeds";
    }

    protected abstract String getCalendarName(Map<String, Object> model);

    protected abstract String getCalendarDescription(Map<String, Object> model);

    protected abstract String getTimeZone(Map<String, Object> model);

    protected abstract List<T> getEvents(Map<String, Object> model);

    protected abstract VEvent mapEvent(T entry, Map<String, Object> model);
}
