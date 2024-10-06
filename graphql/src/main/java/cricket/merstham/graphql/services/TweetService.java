package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static cricket.merstham.graphql.helpers.UriHelper.resolveUrl;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

@Service
public class TweetService {
    private static final Logger LOG = LoggerFactory.getLogger(TweetService.class);
    private static final String TWEET_RESOURCE = "https://api.x.com/2/tweets";
    private static final String TWITTER = "twitter";

    private final TokenService tokenService;

    public TweetService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public String tweet(String message, String link) {
        var template = new RestTemplate();

        var request = Map.of("text", format("{0} {1}", message, link));
        template.setInterceptors(List.of(new TwitterAuthenticationInterceptor(tokenService)));

        var result = template.postForObject(TWEET_RESOURCE, request, JsonNode.class);
        if (!isNull(result)) {
            LOG.info("Tweet returned: {}", result);
            return result.at("/data/id").asText();
        }
        throw new RuntimeException("Null response received from Twitter API");
    }

    public void unTweet(String id) {
        var template = new RestTemplate();

        template.setInterceptors(List.of(new TwitterAuthenticationInterceptor(tokenService)));

        template.delete(resolveUrl(TWEET_RESOURCE, id));
    }

    public static class TwitterAuthenticationInterceptor implements ClientHttpRequestInterceptor {
        private final TokenService tokenService;

        private TwitterAuthenticationInterceptor(TokenService tokenService) {
            this.tokenService = tokenService;
        }

        @NotNull
        @Override
        public ClientHttpResponse intercept(
                HttpRequest request, @NotNull byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            request.getHeaders().setBearerAuth(tokenService.getToken(TWITTER));
            return execution.execute(request, body);
        }
    }
}
