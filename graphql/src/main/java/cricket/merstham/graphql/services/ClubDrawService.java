package cricket.merstham.graphql.services;

import com.gocardless.GoCardlessClient;
import com.gocardless.services.SubscriptionService;
import cricket.merstham.graphql.entity.ClubDrawSubscriptionEntity;
import cricket.merstham.graphql.repository.ClubDrawSubscriptionEntityRepository;
import cricket.merstham.shared.dto.ClubDrawSubscription;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

import static cricket.merstham.graphql.helpers.UserHelper.getSubject;

@Service
public class ClubDrawService {

    private final ModelMapper modelMapper;
    private final ClubDrawSubscriptionEntityRepository clubDrawSubscriptionEntityRepository;
    private final GoCardlessClient goCardlessClient;

    @Autowired
    public ClubDrawService(
            ModelMapper modelMapper,
            ClubDrawSubscriptionEntityRepository clubDrawSubscriptionEntityRepository,
            GoCardlessClient goCardlessClient) {
        this.modelMapper = modelMapper;
        this.clubDrawSubscriptionEntityRepository = clubDrawSubscriptionEntityRepository;
        this.goCardlessClient = goCardlessClient;
    }

    @PreAuthorize("isAuthenticated()")
    public List<ClubDrawSubscription> getSubscriptionsForUser(Principal principal) {
        return clubDrawSubscriptionEntityRepository
                .findAllByOwnerUserId(getSubject(principal))
                .stream()
                .map(t -> modelMapper.map(t, ClubDrawSubscription.class))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public ClubDrawSubscription createSubscription(String mandateId, int noOfTickets) {
        var now = Instant.now();
        var subscription =
                goCardlessClient
                        .subscriptions()
                        .create()
                        .withCurrency("GBP")
                        .withDayOfMonth(1)
                        .withAmount(noOfTickets * 2)
                        .withLinksMandate(mandateId)
                        .withPaymentReference("")
                        .withName("")
                        .withIntervalUnit(
                                SubscriptionService.SubscriptionCreateRequest.IntervalUnit.MONTHLY)
                        .execute();

        var entity =
                ClubDrawSubscriptionEntity.builder()
                        .subscriptionId(subscription.getId())
                        .active(true)
                        .createDate(now)
                        .lastUpdated(now)
                        .ownerUserId("")
                        .noOfTickets(noOfTickets)
                        .build();
        entity = clubDrawSubscriptionEntityRepository.save(entity);
        return modelMapper.map(entity, ClubDrawSubscription.class);
    }
}
