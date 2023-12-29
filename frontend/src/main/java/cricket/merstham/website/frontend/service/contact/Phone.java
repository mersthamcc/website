package cricket.merstham.website.frontend.service.contact;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("phone-contact-method")
public class Phone implements ContactMethod {

    private static final String KEY = "PHONE";
    private final boolean enabled;
    private final String defaultRegion;

    private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

    public Phone(
            @Value("${contact-methods.phone.enabled}") boolean enabled,
            @Value("${contact-methods.phone.default-region}") String defaultRegion) {
        this.enabled = enabled;
        this.defaultRegion = defaultRegion;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<String> validate(String value) {
        try {
            var phoneNumber = phoneUtil.parse(value, defaultRegion);
            return phoneUtil.isValidNumber(phoneNumber)
                    ? List.of()
                    : List.of("contact.PHONE.invalid");
        } catch (NumberParseException e) {
            return List.of("contact.PHONE.parse-error");
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
