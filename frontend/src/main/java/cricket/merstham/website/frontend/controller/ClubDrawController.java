package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.ClubDrawService;
import cricket.merstham.website.frontend.service.MandateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.HashMap;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static cricket.merstham.website.frontend.helpers.UserHelper.getAccessToken;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Controller
@ConditionalOnProperty(name = "features.club-draw")
public class ClubDrawController {

    public static final String CLUB_DRAW_HOME_URL = "/100-club";
    public static final String CLUB_DRAW_JOIN_URL = CLUB_DRAW_HOME_URL + "/join";
    public static final String CLUB_DRAW_SUBSCRIPTION_URL =
            CLUB_DRAW_HOME_URL + "/join/subscription";
    public static final String CLUB_DRAW_NEW_MANDATE_CALLBACK_URL =
            CLUB_DRAW_HOME_URL + "/join/new-mandate";
    private static final String NEW = "new";
    private static final String SESSION_FLOW_ID = "session-flow-id";
    private final ClubDrawService clubDrawService;
    private final MandateService mandateService;

    @Autowired
    public ClubDrawController(ClubDrawService clubDrawService, MandateService mandateService) {
        this.clubDrawService = clubDrawService;
        this.mandateService = mandateService;
    }

    @GetMapping(CLUB_DRAW_HOME_URL)
    public ModelAndView lotteryHome() {
        var model = new HashMap<String, Object>();
        model.put("members", 0);
        model.put("prize_fund", 0.00);
        model.put("first_prize", 0.00);
        model.put("page", clubDrawService.getHomePage());

        return new ModelAndView("club-draw/home", model);
    }

    @GetMapping(CLUB_DRAW_JOIN_URL)
    @PreAuthorize("isAuthenticated()")
    public ModelAndView joinClubDraw(Principal principal) {
        var model = new HashMap<String, Object>();
        var existingMandates = mandateService.getUserMandates(principal);

        model.put("existingMandates", existingMandates);
        return new ModelAndView("club-draw/join", model);
    }

    @PostMapping(CLUB_DRAW_JOIN_URL)
    @PreAuthorize("isAuthenticated()")
    public RedirectView chooseMandate(
            @ModelAttribute("mandate") String mandate,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        if (isNull(mandate) || NEW.equals(mandate)) {
            var redirectFlow =
                    mandateService.createNewMandate(
                            request,
                            CLUB_DRAW_NEW_MANDATE_CALLBACK_URL,
                            request.getSession().getId());
            request.getSession().setAttribute(SESSION_FLOW_ID, redirectFlow.getId());
            return redirectTo(redirectFlow.getRedirectUrl());
        }
        redirectAttributes.addFlashAttribute("mandate", mandate);
        return redirectTo(CLUB_DRAW_SUBSCRIPTION_URL);
    }

    @GetMapping(CLUB_DRAW_NEW_MANDATE_CALLBACK_URL)
    @PreAuthorize("isAuthenticated()")
    public RedirectView newMandateCallback(
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String flowId = (String) request.getSession().getAttribute(SESSION_FLOW_ID);
        if (nonNull(flowId)) {
            var mandate =
                    mandateService.completeRedirectFlow(
                            flowId,
                            request.getSession().getId(),
                            getAccessToken(request.getUserPrincipal()));

            redirectAttributes.addFlashAttribute("mandate", mandate.getId());
            return redirectTo(CLUB_DRAW_SUBSCRIPTION_URL);
        }
        return redirectTo(CLUB_DRAW_JOIN_URL);
    }

    @GetMapping(CLUB_DRAW_SUBSCRIPTION_URL)
    @PreAuthorize("isAuthenticated()")
    public ModelAndView setupSubscription(Principal principal) {
        var model = new HashMap<String, Object>();
        var subscriptionId = "";

        clubDrawService.getMySubscription(principal);

        model.put("subscriptionId", subscriptionId);
        return new ModelAndView("club-draw/subscription", model);
    }
}
