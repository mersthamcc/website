package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.dto.User;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.security.Principal;

import static cricket.merstham.graphql.helpers.UserHelper.getRoles;
import static cricket.merstham.graphql.helpers.UserHelper.getSubject;

@Controller
public class UserInfoController {

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

    @MutationMapping("userInfo")
    @PreAuthorize("isAuthenticated()")
    public User userInfoMutation(Principal principal) {
        return principalToUser(principal);
    }
}
