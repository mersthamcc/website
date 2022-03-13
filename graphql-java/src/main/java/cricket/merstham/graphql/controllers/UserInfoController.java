package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.dto.User;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.stream.Collectors;

@Controller
public class UserInfoController {

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public User userInfo(Principal principal) {
        return principalToUser(principal);
    }

    private User principalToUser(Principal principal) {
        var keycloakToken = ((KeycloakAuthenticationToken) principal);
        return User.builder()
                .username(principal.getName())
                .subjectId(((SimpleKeycloakAccount)keycloakToken.getDetails()).getKeycloakSecurityContext().getToken().getSubject())
                .roles(keycloakToken.getAuthorities()
                        .stream().map(grantedAuthority -> grantedAuthority.getAuthority())
                        .collect(Collectors.toList()))
                .build();
    }

    @MutationMapping("userInfo")
    @PreAuthorize("isAuthenticated()")
    public User userInfoMutation(Principal principal) {
        return principalToUser(principal);
    }
}
