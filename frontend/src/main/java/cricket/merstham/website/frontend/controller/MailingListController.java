package cricket.merstham.website.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MailingListController {

    @GetMapping("/mailing-list")
    public ModelAndView subscribe() {
        return new ModelAndView("mailing-list/subscribe");
    }
}
