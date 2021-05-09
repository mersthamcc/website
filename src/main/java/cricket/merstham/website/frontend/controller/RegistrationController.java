package cricket.merstham.website.frontend.controller;

import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.service.GraphService;
import cricket.merstham.website.graph.MembershipCategoriesQuery;
import cricket.merstham.website.graph.type.StringFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class RegistrationController {

    private GraphService membershipCategoryService;

    @Autowired
    public RegistrationController(GraphService membershipCategoryService) {
        this.membershipCategoryService = membershipCategoryService;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/register", name = "register", method = RequestMethod.GET)
    public ModelAndView register(Principal principal) throws IOException {
        MembershipCategoriesQuery query = new MembershipCategoriesQuery(StringFilter.builder().build());
        Response<MembershipCategoriesQuery.Data> result = membershipCategoryService.executeQuery(query);

        return new ModelAndView(
                "registration/register",
                Map.of("categories", result.getData().membershipCategories())
        );
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/register/select-membership", name = "select-membership", method = RequestMethod.GET)
    public ModelAndView selectMembership(Principal principal) throws IOException {
        MembershipCategoriesQuery query = new MembershipCategoriesQuery(StringFilter.builder().build());
        Response<MembershipCategoriesQuery.Data> result = membershipCategoryService.executeQuery(query);

        return new ModelAndView("registration/register", Map.of("categories", result.getData().membershipCategories()));
    }
}
