package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.website.frontend.configuration.GraphConfiguration;
import cricket.merstham.website.frontend.mappers.CustomGraphQLScalars;
import cricket.merstham.website.frontend.mappers.LocalDateCustomTypeAdapter;
import cricket.merstham.website.frontend.mappers.LocalDateTimeCustomTypeAdapter;
import okio.ByteString;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static javax.ws.rs.core.Response.Status.OK;

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

    public <T extends Query, R> R executeQuery(T query, Principal principal, TypeReference<R> clazz)
            throws IOException {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
        var keycloakPrincipal = (KeycloakPrincipal) token.getPrincipal();

        LOG.info("Sending `{}` GraphQL API request with user token", query.name().name());
        byte[] response =
                getRawResult(
                        query, keycloakPrincipal.getKeycloakSecurityContext().getTokenString());
        return objectMapper.readValue(response, clazz);
    }

    public <T extends Query, R extends Operation.Data> Response<R> executeQuery(
            T query, Principal principal) throws IOException {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
        var keycloakPrincipal = (KeycloakPrincipal) token.getPrincipal();

        LOG.info("Sending `{}` GraphQL API request with user token", query.name().name());
        return getResult(query, keycloakPrincipal.getKeycloakSecurityContext().getTokenString());
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
            T mutation, Principal principal) throws IOException {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
        var keycloakPrincipal = (KeycloakPrincipal) token.getPrincipal();

        return getResult(mutation, keycloakPrincipal.getKeycloakSecurityContext().getTokenString());
    }

    public <T extends Mutation, R> R executeMutation(
            T mutation, Principal principal, Class<R> clazz) throws IOException {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
        var keycloakPrincipal = (KeycloakPrincipal) token.getPrincipal();

        byte[] response =
                getRawResult(
                        mutation, keycloakPrincipal.getKeycloakSecurityContext().getTokenString());
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
        LOG.info("Response Body = {}", body);
        if (response.getStatusInfo().getFamily() == SUCCESSFUL) {
            return body.getBytes();
        }
        throw new RuntimeException("Failed to get GraphQL response from service");
    }
}
