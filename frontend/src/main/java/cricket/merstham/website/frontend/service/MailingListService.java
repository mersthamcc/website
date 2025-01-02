package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.MailingListSubscription;
import cricket.merstham.website.graph.mailingList.MailingListSubscriptionsQuery;
import cricket.merstham.website.graph.mailingList.UpdateMailingListSubscriptionsMutation;
import cricket.merstham.website.graph.type.MailingListSubscriptionInput;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static cricket.merstham.website.frontend.helpers.GraphQLResultHelper.requireGraphData;

@Service
public class MailingListService {

    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public MailingListService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public List<MailingListSubscription> getSubscriptions(List<String> emailAddresses) {
        var request = MailingListSubscriptionsQuery.builder().emailAddress(emailAddresses).build();

        Response<MailingListSubscriptionsQuery.Data> response = graphService.executeQuery(request);
        return requireGraphData(
                        response, MailingListSubscriptionsQuery.Data::getMailingListSubscriptions)
                .stream()
                .map(subscription -> modelMapper.map(subscription, MailingListSubscription.class))
                .toList();
    }

    public List<MailingListSubscription> updateSubscriptions(
            List<MailingListSubscription> subscriptions) {
        var request =
                UpdateMailingListSubscriptionsMutation.builder()
                        .subscriptions(
                                subscriptions.stream()
                                        .map(
                                                subscription ->
                                                        MailingListSubscriptionInput.builder()
                                                                .emailAddress(
                                                                        subscription
                                                                                .getEmailAddress())
                                                                .subscribed(
                                                                        subscription.isSubscribed())
                                                                .build())
                                        .toList())
                        .build();
        Response<UpdateMailingListSubscriptionsMutation.Data> response =
                graphService.executeMutation(request);
        return requireGraphData(
                        response,
                        UpdateMailingListSubscriptionsMutation.Data
                                ::getUpdateMailingListSubscriptions)
                .stream()
                .map(subscription -> modelMapper.map(subscription, MailingListSubscription.class))
                .toList();
    }
}
