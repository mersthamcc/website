package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.Message;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MessageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static cricket.merstham.website.frontend.controller.administration.NewsController.HAS_ROLE_ROLE_NEWS;
import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_MESSAGE_EDIT_ROUTE;

@Controller("adminMessageController")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping(value = ADMIN_MESSAGE_EDIT_ROUTE, name = "admin-message-edit")
    @PreAuthorize(HAS_ROLE_ROLE_NEWS)
    public ModelAndView edit(@PathVariable("key") String key) throws IOException {
        var message = service.getMessage(key);
        Map<String, Object> model = new HashMap<>();
        model.put("message", message);

        return new ModelAndView("administration/message/edit", model);
    }

    @PostMapping(value = ADMIN_MESSAGE_EDIT_ROUTE, name = "admin-message-save")
    @PreAuthorize(HAS_ROLE_ROLE_NEWS)
    public RedirectView save(
            @PathVariable("key") String key,
            Message message,
            CognitoAuthentication cognitoAuthentication)
            throws IOException {
        if (message.getKey().equals(key)) {
            var result = service.saveMessage(cognitoAuthentication.getOAuth2AccessToken(), message);
            return redirectTo(result.getKey());
        }
        return redirectTo(message.getKey());
    }
}
