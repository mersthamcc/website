//package cricket.merstham.website.frontend.controller.administration;
//
//import cricket.merstham.website.frontend.service.MembershipService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Controller("AdminMembershipController")
//public class MembershipController {
//
//    private final MembershipService membershipService;
//
//    @Autowired
//    public MembershipController(MembershipService membershipService) {
//        this.membershipService = membershipService;
//    }
//
//    @GetMapping(value = "/administration/membership", name = "admin-membership-home")
//    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
//    public ModelAndView list() {
//        Map<String, Object> model = new HashMap<>();
//        model.put("members", membershipService.getAllMembers());
//        return new ModelAndView("administration/membership/list", model);
//    }
//}
