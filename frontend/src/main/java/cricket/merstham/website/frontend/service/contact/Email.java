package cricket.merstham.website.frontend.service.contact;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service("email-contact-method")
public class Email implements ContactMethod {
    private static final String KEY = "EMAIL";
    private boolean enabled;
    private static final Pattern PATTERN =
            Pattern.compile(
                    "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                            + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$");

    public Email(@Value("${contact-methods.email.enabled}") boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<String> validate(String value) {
        return PATTERN.matcher(value).matches() ? List.of() : List.of("contact.EMAIL.invalid");
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
