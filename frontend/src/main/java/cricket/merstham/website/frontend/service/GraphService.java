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
import cricket.merstham.website.frontend.mappers.LocalDateCustomTypeAdapter;
import cricket.merstham.website.frontend.mappers.LocalDateTimeCustomTypeAdapter;
import okio.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

import java.io.IOException;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

@Service
public class GraphService {
    private static final Logger LOG = LoggerFactory.getLogger(GraphService.class);

    private final GraphConfiguration graphConfiguration;
    private final AccessTokenManager accessTokenManager;
    private final ObjectMapper objectMapper;
    private final ScalarTypeAdapters adapters =
            new ScalarTypeAdapters(
                    Map.of(
                            CustomGraphQLScalars.DATE, new LocalDateCustomTypeAdapter(),
                            CustomGraphQLScalars.DATETIME, new LocalDateTimeCustomTypeAdapter()));

    @Autowired
    public GraphService(
            GraphConfiguration graphConfiguration,
            AccessTokenManager accessTokenManager,
            ObjectMapper objectMapper) {
        this.graphConfiguration = graphConfiguration;
        this.accessTokenManager = accessTokenManager;
        this.objectMapper = objectMapper;
    }

    public <T extends Query, R> R executeQuery(
            T query, OAuth2AccessToken accessToken, TypeReference<R> clazz) throws IOException {

        LOG.info("Sending `{}` GraphQL API request with user token", query.name().name());
        byte[] response = getRawResult(query, accessToken.getTokenValue());
        return objectMapper.readValue(response, clazz);
    }

    public <T extends Query, R extends Operation.Data> Response<R> executeQuery(
            T query, OAuth2AccessToken accessToken) throws IOException {

        LOG.info("Sending `{}` GraphQL API request with user token", query.name().name());
        return getResult(query, accessToken.getTokenValue());
    }

    public <T extends Query, R extends Operation.Data> Response<R> executeQuery(T query)
            throws IOException {
        String accessToken = accessTokenManager.getAccessToken();
        LOG.info(
                "Sending `{}` GraphQL API request with client credentials token",
                query.name().name());
        return getResult(query, accessToken);
    }

    public <T extends Mutation, R extends Operation.Data> Response<R> executeMutation(
            T mutation, OAuth2AccessToken accessToken) throws IOException {

        return getResult(mutation, accessToken.getTokenValue());
    }

    public <T extends Mutation, R> R executeMutation(
            T mutation, OAuth2AccessToken accessToken, Class<R> clazz) throws IOException {
        byte[] response = getRawResult(mutation, accessToken.getTokenValue());
        return objectMapper.readValue(response, clazz);
    }

    private <T extends Operation, R extends Operation.Data> Response<R> getResult(
            T query, String accessToken) throws IOException {
        return query.parse(ByteString.of(getRawResult(query, accessToken)), adapters);
    }

    private <T extends Operation> byte[] getRawResult(T query, String accessToken)
            throws IOException {
        var client = ClientBuilder.newClient();
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
