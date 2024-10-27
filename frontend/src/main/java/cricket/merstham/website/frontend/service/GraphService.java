package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Mutation;
import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.ScalarTypeAdapters;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.website.frontend.configuration.GraphConfiguration;
import cricket.merstham.website.frontend.mappers.CustomGraphQLScalars;
import cricket.merstham.website.frontend.mappers.InstantCustomTypeAdapter;
import cricket.merstham.website.frontend.mappers.JsonNodeCustomTypeAdapter;
import cricket.merstham.website.frontend.mappers.LocalDateCustomTypeAdapter;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.Map;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;

@Service
public class GraphService {
    private static final Logger LOG = LoggerFactory.getLogger(GraphService.class);

    private final GraphConfiguration graphConfiguration;
    private final AccessTokenManager accessTokenManager;
    private final ObjectMapper objectMapper;
    private final Client client;
    private final ScalarTypeAdapters adapters =
            new ScalarTypeAdapters(
                    Map.of(
                            CustomGraphQLScalars.DATE, new LocalDateCustomTypeAdapter(),
                            CustomGraphQLScalars.DATETIME, new InstantCustomTypeAdapter(),
                            CustomGraphQLScalars.JSON, new JsonNodeCustomTypeAdapter()));

    @Autowired
    public GraphService(
            GraphConfiguration graphConfiguration,
            AccessTokenManager accessTokenManager,
            ObjectMapper objectMapper,
            @Named("graphqlClient") Client client) {
        this.graphConfiguration = graphConfiguration;
        this.accessTokenManager = accessTokenManager;
        this.objectMapper = objectMapper;
        this.client = client;
    }

    public <T extends Query, R> R executeQuery(
            T query, OAuth2AccessToken accessToken, TypeReference<R> clazz) {

        LOG.info("Sending `{}` GraphQL API request with user token", query.name().name());
        byte[] response = getRawResult(query, accessToken.getTokenValue());
        try {
            return objectMapper.readValue(response, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Query, R extends Operation.Data> Response<R> executeQuery(
            T query, OAuth2AccessToken accessToken) {

        LOG.info("Sending `{}` GraphQL API request with user token", query.name().name());
        return getResult(query, accessToken.getTokenValue());
    }

    public <T extends Query, R extends Operation.Data> Response<R> executeQuery(T query) {
        String accessToken = accessTokenManager.getAccessToken();
        LOG.info(
                "Sending `{}` GraphQL API request with client credentials token",
                query.name().name());
        return getResult(query, accessToken);
    }

    public <T extends Mutation, R extends Operation.Data> Response<R> executeMutation(
            T mutation, OAuth2AccessToken accessToken) {

        return getResult(mutation, accessToken.getTokenValue());
    }

    public <T extends Mutation, R> R executeMutation(
            T mutation, OAuth2AccessToken accessToken, Class<R> clazz) {
        byte[] response = getRawResult(mutation, accessToken.getTokenValue());
        try {
            return objectMapper.readValue(response, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Operation, R extends Operation.Data> Response<R> getResult(
            T query, String accessToken) {
        try {
            return query.parse(ByteString.of(getRawResult(query, accessToken)), adapters);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends Operation> byte[] getRawResult(T query, String accessToken) {
        var webTarget = client.target(graphConfiguration.getGraphUri());

        var invocation =
                webTarget
                        .request(APPLICATION_JSON_TYPE)
                        .accept(APPLICATION_JSON_TYPE)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                        .buildPost(Entity.json(query.composeRequestBody(adapters).utf8()));
        var response = invocation.invoke();
        LOG.info(
                "Received `{}` GraphQL API response: {}",
                query.name().name(),
                response.getStatus());
        String body = response.readEntity(String.class);
        LOG.debug("Response Body = {}", body);
        if (response.getStatusInfo().getFamily() == SUCCESSFUL) {
            return body.getBytes();
        }
        throw new RuntimeException("Failed to get GraphQL response from service");
    }
}
