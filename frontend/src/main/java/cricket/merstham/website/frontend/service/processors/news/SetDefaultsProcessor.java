package cricket.merstham.website.frontend.service.processors.news;

import cricket.merstham.website.frontend.model.News;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service("NewsDefaults")
public class SetDefaultsProcessor implements ItemProcessor<News> {
    private static Logger LOG = LoggerFactory.getLogger(SetDefaultsProcessor.class);

    @Override
    public List<String> preSave(News item) {
        LOG.info("Setting defaults on news item '{}'", item.getTitle());
        if (isNull(item.getCreatedDate())) item.setCreatedDate(LocalDateTime.now());
        if (isNull(item.getPublishDate())) item.setPublishDate(item.getCreatedDate());
        if (isNull(item.getUuid())) item.setUuid(UUID.randomUUID().toString());
        return List.of();
    }
}
