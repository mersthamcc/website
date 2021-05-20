package cricket.merstham.website.frontend.controller;

import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.model.RegistrationAction;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.model.Subscription;
import cricket.merstham.website.frontend.service.GraphService;
import cricket.merstham.website.graph.MembershipCategoriesQuery;
import cricket.merstham.website.graph.type.StringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@SessionAttributes("basket")
@PreAuthorize("isAuthenticated()")
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    private GraphService membershipCategoryService;

    @Autowired
    public RegistrationController(GraphService membershipCategoryService) {
        this.membershipCategoryService = membershipCategoryService;
    }

    @ModelAttribute("basket")
    public RegistrationBasket createRegistrationBasket() {
        LOG.info("Creating new registration basket");
        return new RegistrationBasket();
    }

    @RequestMapping(value = "/register", name = "register", method = RequestMethod.GET)
    public ModelAndView register(
            @ModelAttribute("basket") RegistrationBasket basket) {
        return new ModelAndView(
                "registration/register",
                Map.of("basket", basket)
        );
    }

    @RequestMapping(
            value = "/register",
            name = "registration-actions",
            method = RequestMethod.POST)
    public View actionProcessor(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("action") String action,
            @ModelAttribute("delete-member") String deleteMember,
            @ModelAttribute("edit-member") String editMember
    ) {
        if (!deleteMember.isBlank()) {
            basket.removeSuscription(UUID.fromString(deleteMember));
        } else if (!editMember.isBlank()) {

        } else {
            switch (action) {
                case "add-member":
                    return new RedirectView("/register/select-membership");
            }
        }
        return new RedirectView("/register");
    }

    @RequestMapping(value = "/register/select-membership", name = "select-membership", method = RequestMethod.GET)
    public ModelAndView selectMembership(
            @ModelAttribute("basket") RegistrationBasket basket
    ) throws IOException {
        MembershipCategoriesQuery query = new MembershipCategoriesQuery(StringFilter.builder().build());
        Response<MembershipCategoriesQuery.Data> result = membershipCategoryService.executeQuery(query);

        return new ModelAndView(
                "registration/select-membership",
                Map.of("categories", result.getData().membershipCategories())
        );
    }

    @RequestMapping(value = "/register/select-membership", name = "member-details", method = RequestMethod.POST)
    public ModelAndView membershipForm(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("category") String category,
            @ModelAttribute("pricelistitemid") Integer priceListItemId
    ) {
        MembershipCategoriesQuery query = new MembershipCategoriesQuery(
                StringFilter
                        .builder()
                        .equals(category)
        .build());
        try {
            Response<MembershipCategoriesQuery.Data> result = membershipCategoryService.executeQuery(query);

            Subscription subscription = new Subscription();
            MembershipCategoriesQuery.MembershipCategory membershipCategory = result.getData().membershipCategories().get(0);
            MembershipCategoriesQuery.PricelistItem pricelistItem = membershipCategory
                    .pricelistItem()
                    .stream().filter(p -> p.id() == priceListItemId)
                    .findFirst()
                    .orElseThrow();

            subscription
                    .setUuid(UUID.randomUUID())
                    .setAction(RegistrationAction.NEW)
                    .setMember(new HashMap<>())
                    .setPricelistItemId(priceListItemId)
                    .setPrice(BigDecimal.valueOf(pricelistItem.currentPrice()))
                    .updateDefinition(membershipCategory);

            basket.addSubscription(subscription);
            return new ModelAndView(
                    "registration/membership-form",
                    Map.of(
                            "form", membershipCategory.form(),
                            "subscription", subscription
                    )
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/register/add-member", name = "member-details", method = RequestMethod.POST)
    public View membershipForm(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("subscription") Subscription subscription
    ) {
        basket.updateSubscription(subscription);

        return new RedirectView("/register");
    }
}
