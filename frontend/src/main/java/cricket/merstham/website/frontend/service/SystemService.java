package cricket.merstham.website.frontend.service;

import cricket.merstham.shared.dto.AuthResult;
import cricket.merstham.website.graph.system.GetAuthRequestQuery;
import cricket.merstham.website.graph.system.PutAuthCodeMutation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static cricket.merstham.website.frontend.helpers.GraphQLResultHelper.requireGraphData;

@Service
public class SystemService {

    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public SystemService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public String getAuthUrl(String name, String redirectUrl, OAuth2AccessToken accessToken)
            throws IOException {
        var query = GetAuthRequestQuery.builder().name(name).redirectUrl(redirectUrl).build();
        return requireGraphData(
                        graphService.executeQuery(query, accessToken),
                        GetAuthRequestQuery.Data::getGetAuthRequest)
                .getUrl();
    }

    public AuthResult putAuthCode(
            String name,
            String state,
            String code,
            String redirectUrl,
            OAuth2AccessToken accessToken)
            throws IOException {
        var mutation =
                PutAuthCodeMutation.builder()
                        .name(name)
                        .state(state)
                        .code(code)
                        .redirectUrl(redirectUrl)
                        .build();
        return modelMapper.map(
                requireGraphData(
                        graphService.executeMutation(mutation, accessToken),
                        PutAuthCodeMutation.Data::getPutAuthCode),
                AuthResult.class);
    }
}
