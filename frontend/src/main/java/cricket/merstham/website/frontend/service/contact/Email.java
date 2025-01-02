package cricket.merstham.website.frontend.service.contact;

import cricket.merstham.website.frontend.service.EmailAddressValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("email-contact-method")
public class Email implements ContactMethod {
    private static final String KEY = "EMAIL";
    private boolean enabled;
    private final EmailAddressValidator emailAddressValidator;

    public Email(
            @Value("${contact-methods.email.enabled}") boolean enabled,
            EmailAddressValidator emailAddressValidator) {
        this.enabled = enabled;
        this.emailAddressValidator = emailAddressValidator;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<String> validate(String value) {
        return emailAddressValidator.validate(value) ? List.of() : List.of("contact.EMAIL.invalid");
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
