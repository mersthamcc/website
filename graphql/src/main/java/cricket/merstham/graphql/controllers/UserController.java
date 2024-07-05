package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.CognitoService;
import cricket.merstham.shared.dto.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static cricket.merstham.graphql.helpers.UserHelper.getRoles;
import static cricket.merstham.graphql.helpers.UserHelper.getSubject;

@Controller
public class UserController {

    private final CognitoService cognitoService;

    public UserController(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public User userInfo(Principal principal) {
        return principalToUser(principal);
    }

    private User principalToUser(Principal principal) {
        return User.builder()
                .username(principal.getName())
                .subjectId(getSubject(principal))
                .roles(getRoles(principal))
                .build();
    }

    @QueryMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User getUser(@Argument("username") String username) {
        return cognitoService.getUserDetails(username);
    }
}
