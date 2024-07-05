package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.User;
import cricket.merstham.website.graph.GetUserQuery;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserService {

    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public User getUser(String username, OAuth2AccessToken accessToken) throws IOException {
        var query = GetUserQuery.builder().username(username).build();
        Response<GetUserQuery.Data> response = graphService.executeQuery(query, accessToken);
        return modelMapper.map(response.getData().getGetUser(), User.class);
    }
}
