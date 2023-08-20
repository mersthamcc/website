package cricket.merstham.website.frontend.validators;

import cricket.merstham.website.frontend.service.CognitoService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailNotInUseValidator implements ConstraintValidator<EmailNotInUse, String> {

    private final CognitoService service;

    @Autowired
    public EmailNotInUseValidator(CognitoService service) {
        this.service = service;
    }

    @Override
    public void initialize(EmailNotInUse constraintAnnotation) {}

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !service.userExists(email);
    }
}
