package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.ClubDrawService;
import cricket.merstham.shared.dto.ClubDrawSubscription;
import cricket.merstham.shared.dto.ClubDrawWinner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class ClubDrawController {

    private final ClubDrawService service;

    @Autowired
    public ClubDrawController(ClubDrawService service) {
        this.service = service;
    }

    @QueryMapping("myClubDrawSubscriptions")
    public List<ClubDrawSubscription> getMyClubSubscriptions(Principal principal) {
        return service.getSubscriptionsForUser(principal);
    }

    @QueryMapping("clubDrawWinners")
    public List<ClubDrawWinner> getLotteryWinners() {
        return List.of();
    }

    @MutationMapping("addClubDrawSubscription")
    public ClubDrawSubscription addLotteryTicket(
            @Argument("mandateId") String mandateId, @Argument("noOfTickets") int noOfTickets) {
        return service.createSubscription(mandateId, noOfTickets);
    }

    @MutationMapping("disableTicket")
    public ClubDrawSubscription disableLotteryTicket(@Argument("id") int id) {
        return null;
    }
}
