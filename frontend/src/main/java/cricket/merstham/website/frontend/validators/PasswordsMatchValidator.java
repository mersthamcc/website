package cricket.merstham.website.frontend.validators;

import cricket.merstham.website.frontend.model.UserSignUp;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Objects;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {

    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {}

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof UserSignUp) {
            UserSignUp user = (UserSignUp) obj;
            return Objects.equals(user.getPassword(), user.getConfirmPassword());
        }
        return false;
    }
}
