package cricket.merstham.website.frontend.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private final org.passay.PasswordValidator validator;

    public PasswordValidator() {
        this.validator =
                new org.passay.PasswordValidator(
                        new LengthRule(8, 100),
                        new CharacterRule(EnglishCharacterData.UpperCase, 1),
                        new CharacterRule(EnglishCharacterData.LowerCase, 1),
                        new CharacterRule(EnglishCharacterData.Digit, 1));
    }

    @Override
    public void initialize(Password constraintAnnotation) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return validator.validate(new PasswordData(password)).isValid();
    }
}
