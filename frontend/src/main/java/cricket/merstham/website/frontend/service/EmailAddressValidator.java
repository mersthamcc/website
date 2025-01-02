package cricket.merstham.website.frontend.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Service
public class EmailAddressValidator {
    private static final Pattern PATTERN =
            Pattern.compile(
                    "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                            + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$");

    public boolean validate(String value) {
        if (isNull(value)) return false;
        return PATTERN.matcher(value).matches();
    }
}
