package cricket.merstham.graphql.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.shared.dto.MailingListSubscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MailingListService {
    private static final Logger LOG = LoggerFactory.getLogger(MailingListService.class);

    private static final String BASE_URI = "https://{dc}.api.mailchimp.com/3.0";
    private static final String MEMBER_ENDPOINT = "/lists/{listId}/members/{subscriberHash}";
    private static final String MEMBER_TAG_ENDPOINT =
            "/lists/{listId}/members/{subscriberHash}/tags";

    private final String dc;
    private final String listId;
    private final String newsLetterInterestId;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public MailingListService(
            @Value("${configuration.mailchimp.api-key}") String apiKey,
            @Value("${configuration.mailchimp.dc}") String dc,
            @Value("${configuration.mailchimp.list-id}") String listId,
            @Value("${configuration.mailchimp.newsletter-interest-id}") String newsLetterInterestId,
            ObjectMapper objectMapper) {
        this.dc = dc;
        this.listId = listId;
        this.newsLetterInterestId = newsLetterInterestId;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("anystring", apiKey));
    }

    @PreAuthorize("hasAuthority('TRUSTED_CLIENT')")
    public List<MailingListSubscription> getSubscriptionStatus(List<String> emailAddresses) {
        var result = new ArrayList<MailingListSubscription>();

        emailAddresses.forEach(
                emailAddress -> {
                    var subscription = new MailingListSubscription();
                    subscription.setEmailAddress(emailAddress);
                    try {
                        var parameters = new HashMap<String, Object>();
                        parameters.put("dc", this.dc);
                        parameters.put("listId", this.listId);
                        parameters.put("subscriberHash", subscriberHash(emailAddress));
                        ResponseEntity<String> response =
                                restTemplate.exchange(
                                        BASE_URI + MEMBER_ENDPOINT,
                                        HttpMethod.GET,
                                        null,
                                        String.class,
                                        parameters);
                        JsonNode responseBody = objectMapper.readTree(response.getBody());
                        subscription.setSubscribed(
                                responseBody.get("status").asText().equals("subscribed"));
                        subscription.setPending(
                                responseBody.get("status").asText().equals("pending"));
                    } catch (HttpClientErrorException e) {
                        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                            subscription.setSubscribed(false);
                        } else {
                            throw new RuntimeException(e);
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    result.add(subscription);
                });

        return result;
    }

    @PreAuthorize("hasAuthority('TRUSTED_CLIENT')")
    public List<MailingListSubscription> updateSubscriptionStatus(
            List<MailingListSubscription> subscriptions) {
        var result = new ArrayList<MailingListSubscription>();

        subscriptions.forEach(
                subscription -> {
                    try {
                        var parameters = new HashMap<String, Object>();
                        parameters.put("dc", this.dc);
                        parameters.put("listId", this.listId);
                        parameters.put(
                                "subscriberHash", subscriberHash(subscription.getEmailAddress()));

                        if (subscription.isSubscribed()) {
                            createOrUpdateUser(subscription, parameters);
                        } else {
                            deleteUser(subscription, parameters);
                        }
                    } catch (HttpClientErrorException e) {
                        if (e.getStatusCode() == HttpStatus.BAD_REQUEST
                                && e.getMessage().contains("Forgotten Email Not Subscribed")) {
                            subscription.setManualSubscriptionRequired(true);
                            subscription.setSubscribed(false);
                            subscription.setPending(false);
                        } else {
                            throw new RuntimeException(e);
                        }
                    }
                    result.add(subscription);
                });

        return result;
    }

    private void deleteUser(
            MailingListSubscription subscription, HashMap<String, Object> parameters) {
        try {
            LOG.info("Archiving subscriber {}", parameters.get("subscriberHash"));
            restTemplate.exchange(
                    BASE_URI + MEMBER_ENDPOINT, HttpMethod.DELETE, null, String.class, parameters);
        } catch (HttpClientErrorException e) {
            LOG.warn("Request to delete unsubscribed e-mail address", e);
        }
    }

    private void createOrUpdateUser(
            MailingListSubscription subscription, HashMap<String, Object> parameters) {
        var request =
                new HttpEntity<>(
                        Map.of(
                                "email_address", subscription.getEmailAddress(),
                                "status_if_new", "pending",
                                "status", "subscribed",
                                "email_type", "html",
                                "interests", Map.of(newsLetterInterestId, true)));
        ResponseEntity<String> response =
                restTemplate.exchange(
                        BASE_URI + MEMBER_ENDPOINT,
                        HttpMethod.PUT,
                        request,
                        String.class,
                        parameters);

        var tagRequest =
                new HttpEntity<>(
                        Map.of(
                                "tags",
                                List.of(Map.of("name", "registration-form", "status", "active")),
                                "is_syncing",
                                true));
        restTemplate.exchange(
                BASE_URI + MEMBER_TAG_ENDPOINT,
                HttpMethod.POST,
                tagRequest,
                String.class,
                parameters);
        try {
            JsonNode responseBody = objectMapper.readTree(response.getBody());
            subscription.setSubscribed(responseBody.get("status").asText().equals("subscribed"));
            subscription.setPending(responseBody.get("status").asText().equals("pending"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String subscriberHash(String emailAddress) {
        return DigestUtils.md5DigestAsHex(emailAddress.getBytes());
    }
}
