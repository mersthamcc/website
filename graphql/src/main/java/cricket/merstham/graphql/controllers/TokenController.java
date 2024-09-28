package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.TokenService;
import cricket.merstham.shared.dto.AuthRequest;
import cricket.merstham.shared.dto.AuthResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TokenController {

    private final TokenService tokenService;

    @Autowired
    public TokenController(TokenService TokenService) {
        this.tokenService = TokenService;
    }

    @QueryMapping
    public AuthRequest getAuthRequest(@Argument String name, @Argument String redirectUrl) {
        return tokenService.getAuthRequest(name, redirectUrl);
    }

    @MutationMapping
    public AuthResult putAuthCode(
            @Argument String name,
            @Argument String code,
            @Argument String state,
            @Argument String redirectUrl) {
        return tokenService.putAuthCode(name, code, state, redirectUrl);
    }
}
