package cricket.merstham.website.frontend.service.processors.event;

import cricket.merstham.shared.dto.Event;
import cricket.merstham.website.frontend.helpers.RoutesHelper;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.isNull;

@Service("EventDefaults")
public class SetDefaultsProcessor implements ItemProcessor<Event> {
    private static final Logger LOG = LoggerFactory.getLogger(SetDefaultsProcessor.class);

    @Override
    public List<String> preSave(Event item) {
        LOG.info("Setting defaults on event '{}'", item.getTitle());
        if (isNull(item.getUuid())) item.setUuid(UUID.randomUUID().toString());
        var eventDate = item.getEventDate().atZone(UTC);
        item.setPath(
                RoutesHelper.buildRoute(
                                RoutesHelper.EVENTS_ROUTE_TEMPLATE,
                                Map.of(
                                        "year", eventDate.getYear(),
                                        "month", eventDate.format(ofPattern("MM")),
                                        "day", eventDate.format(ofPattern("dd")),
                                        "slug", item.getSlug()))
                        .getPath());
        return List.of();
    }
}
