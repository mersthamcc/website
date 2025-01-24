package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.PassGeneratorService;
import cricket.merstham.shared.dto.Pass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.Principal;

@Controller
public class PassController {

    private final PassGeneratorService passGeneratorService;

    @Autowired
    public PassController(PassGeneratorService passGeneratorService) {
        this.passGeneratorService = passGeneratorService;
    }

    @QueryMapping
    public Pass pass(@Argument String memberUuid, @Argument String type, Principal principal)
            throws IOException {
        return passGeneratorService.getPassData(memberUuid, type, principal);
    }
}
