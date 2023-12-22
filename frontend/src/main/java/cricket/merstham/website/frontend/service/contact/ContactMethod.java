package cricket.merstham.website.frontend.service.contact;

import java.util.List;

public interface ContactMethod {
    String getKey();
    List<String> validate(String value);
    boolean isEnabled();
}
