package cricket.merstham.website.frontend.service.processors.contacts;

import com.google.common.base.Strings;
import cricket.merstham.shared.dto.Contact;
import cricket.merstham.shared.extensions.StringExtensions;
import cricket.merstham.website.frontend.service.contact.ContactMethodManager;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import lombok.experimental.ExtensionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service("ContactMethodValidation")
@ExtensionMethod({StringExtensions.class})
public class ContactMethodValidation implements ItemProcessor<Contact> {
    private static final Logger LOG = LoggerFactory.getLogger(ContactMethodValidation.class);

    private final ContactMethodManager contactMethodManager;

    @Autowired
    public ContactMethodValidation(ContactMethodManager contactMethodManager) {
        this.contactMethodManager = contactMethodManager;
    }

    @Override
    public List<String> preSave(Contact item) {
        LOG.info("Validating methods on contact '{}'", item.getPosition());

        if (isNull(item.getMethods())) return List.of();
        return item.getMethods().stream()
                .filter(m -> !Strings.isNullOrEmpty(m.getValue()))
                .flatMap(
                        m ->
                                contactMethodManager
                                        .getMethodByKey(m.getKey())
                                        .validate(m.getValue())
                                        .stream())
                .toList();
    }
}
