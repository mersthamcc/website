package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.MailingListService;
import cricket.merstham.shared.dto.MailingListSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MailingListController {
    private final MailingListService service;

    @Autowired
    public MailingListController(MailingListService service) {
        this.service = service;
    }

    @QueryMapping
    public List<MailingListSubscription> mailingListSubscriptions(
            @Argument("emailAddress") List<String> emailAddress) {
        return service.getSubscriptionStatus(emailAddress);
    }

    @MutationMapping
    public List<MailingListSubscription> updateMailingListSubscriptions(
            @Argument("subscriptions") List<MailingListSubscription> subscriptions) {
        return service.updateSubscriptionStatus(subscriptions);
    }
}
