package cricket.merstham.graphql.services.webhooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WebhookProcessorManager {

    private final Map<String, WebhookProcessor> processorMap;

    @Autowired
    public WebhookProcessorManager(List<WebhookProcessor> processors) {
        this.processorMap =
                processors.stream()
                        .collect(Collectors.toMap(WebhookProcessor::getName, Function.identity()));
    }

    public WebhookProcessor getProcessor(String name) {
        return processorMap.get(name);
    }

    public boolean exists(String type) {
        return processorMap.containsKey(type);
    }
}
