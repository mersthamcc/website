package cricket.merstham.website.frontend.service.processors.news;

import cricket.merstham.website.frontend.model.News;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("NewsFacebook")
public class FacebookNewsProcessor implements ItemProcessor<News> {
    private static Logger LOG = LoggerFactory.getLogger(FacebookNewsProcessor.class);

    @Override
    public void postProcessing(News item) {
        LOG.info("Running Facebook processor on news item '{}'", item.getTitle());
    }
}
