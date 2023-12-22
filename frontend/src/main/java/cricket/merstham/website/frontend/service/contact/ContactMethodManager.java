package cricket.merstham.website.frontend.service.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContactMethodManager {
    private final Map<String, ContactMethod> contactMethodMap;

    @Autowired
    public ContactMethodManager(List<ContactMethod> contactMethods) {
        this.contactMethodMap =
                contactMethods.stream()
                        .collect(Collectors.toMap(ContactMethod::getKey, Function.identity()));
    }

    public ContactMethod getMethodByKey(String name) {
        return contactMethodMap.get(name);
    }

    public List<String> getAvailableMethods() {
        return contactMethodMap
                .values()
                .stream()
                .filter(ContactMethod::isEnabled)
                .map(ContactMethod::getKey)
                .toList();
    }
}
