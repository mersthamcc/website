package cricket.merstham.website.frontend.controller;

import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.service.GraphService;
import cricket.merstham.website.graph.MembershipCategoriesQuery;
import cricket.merstham.website.graph.type.StringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

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

    @RequestMapping(value = "/register/select-membership", name = "select-membership", method = RequestMethod.GET)
    public ModelAndView selectMembership() throws IOException {
        MembershipCategoriesQuery query = new MembershipCategoriesQuery(StringFilter.builder().build());
        Response<MembershipCategoriesQuery.Data> result = membershipCategoryService.executeQuery(query);

        return new ModelAndView("registration/select-membership", Map.of("categories", result.getData().membershipCategories()));
    }
}
