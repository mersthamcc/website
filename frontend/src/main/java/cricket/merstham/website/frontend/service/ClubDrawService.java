package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.ClubDrawSubscription;
import cricket.merstham.shared.dto.StaticPage;
import cricket.merstham.website.graph.clubDraw.MyClubDrawSubscriptionsQuery;
import cricket.merstham.website.graph.draws.ClubDrawHomeQuery;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

import static cricket.merstham.website.frontend.helpers.GraphQLResultHelper.requireGraphData;
import static cricket.merstham.website.frontend.helpers.UserHelper.getAccessToken;

@Service
public class ClubDrawService {
    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public ClubDrawService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public StaticPage getHomePage() {
        var query = ClubDrawHomeQuery.builder().build();
        Response<ClubDrawHomeQuery.Data> response = null;

        response = graphService.executeQuery(query);
        return modelMapper.map(
                requireGraphData(response, ClubDrawHomeQuery.Data::getHomePage), StaticPage.class);
    }

    public ClubDrawSubscription getMySubscription(Principal principal) {
        Response<MyClubDrawSubscriptionsQuery.Data> response =
                graphService.executeQuery(
                        MyClubDrawSubscriptionsQuery.builder().build(), getAccessToken(principal));

        return modelMapper.map(
                requireGraphData(
                        response,
                        data ->
                                data.getMyClubDrawSubscriptions().stream()
                                        .filter(
                                                MyClubDrawSubscriptionsQuery.MyClubDrawSubscription
                                                        ::isActive)
                                        .findFirst()
                                        .orElse(null)),
                ClubDrawSubscription.class);
    }
}
