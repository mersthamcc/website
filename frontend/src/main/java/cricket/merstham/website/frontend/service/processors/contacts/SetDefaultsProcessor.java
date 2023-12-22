package cricket.merstham.website.frontend.service.processors.contacts;

import cricket.merstham.shared.dto.Contact;
import cricket.merstham.shared.extensions.StringExtensions;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import lombok.experimental.ExtensionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ContactDefaults")
@ExtensionMethod({StringExtensions.class})
public class SetDefaultsProcessor implements ItemProcessor<Contact> {
    private static final Logger LOG = LoggerFactory.getLogger(SetDefaultsProcessor.class);

    @Override
    public List<String> preSave(Contact item) {
        LOG.info("Setting defaults on contact '{}'", item.getPosition());
        item.setSlug(item.getPosition().toSlug());
        return List.of();
    }
}
