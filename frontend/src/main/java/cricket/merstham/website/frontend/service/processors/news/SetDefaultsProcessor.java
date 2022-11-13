package cricket.merstham.website.frontend.service.processors.news;

import cricket.merstham.shared.dto.News;
import cricket.merstham.website.frontend.helpers.RoutesHelper;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.isNull;

@Service("NewsDefaults")
public class SetDefaultsProcessor implements ItemProcessor<News> {
    private static final Logger LOG = LoggerFactory.getLogger(SetDefaultsProcessor.class);

    @Override
    public List<String> preSave(News item) {
        LOG.info("Setting defaults on news item '{}'", item.getTitle());
        if (isNull(item.getCreatedDate())) item.setCreatedDate(Instant.now());
        if (isNull(item.getPublishDate())) item.setPublishDate(item.getCreatedDate());
        if (isNull(item.getUuid())) item.setUuid(UUID.randomUUID().toString());
        item.setPath(
                RoutesHelper.buildRoute(
                                RoutesHelper.NEWS_ROUTE_TEMPLATE,
                                Map.of(
                                        "year", item.getPublishDate().atZone(UTC).getYear(),
                                        "month",
                                                item.getPublishDate()
                                                        .atZone(UTC)
                                                        .format(ofPattern("MM")),
                                        "day",
                                                item.getPublishDate()
                                                        .atZone(UTC)
                                                        .format(ofPattern("dd")),
                                        "slug", item.getSlug()))
                        .getPath());
        return List.of();
    }
}
