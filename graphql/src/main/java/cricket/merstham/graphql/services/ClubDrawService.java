package cricket.merstham.graphql.services;

import cricket.merstham.graphql.repository.ClubDrawSubscriptionEntityRepository;
import cricket.merstham.shared.dto.ClubDrawSubscription;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

import static cricket.merstham.graphql.helpers.UserHelper.getSubject;

@Service
public class ClubDrawService {

    private final ModelMapper modelMapper;
    private final ClubDrawSubscriptionEntityRepository clubDrawSubscriptionEntityRepository;

    @Autowired
    public ClubDrawService(
            ModelMapper modelMapper,
            ClubDrawSubscriptionEntityRepository clubDrawSubscriptionEntityRepository) {
        this.modelMapper = modelMapper;
        this.clubDrawSubscriptionEntityRepository = clubDrawSubscriptionEntityRepository;
    }

    @PreAuthorize("isAuthenticated()")
    public List<ClubDrawSubscription> getSubscriptionsForUser(Principal principal) {
        return clubDrawSubscriptionEntityRepository
                .findAllByOwnerUserId(getSubject(principal))
                .stream()
                .map(t -> modelMapper.map(t, ClubDrawSubscription.class))
                .toList();
    }
}
