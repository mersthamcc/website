package cricket.merstham.website.frontend.configuration;

import lombok.Data;

@Data
public class MailingListConfiguration {
    private final String postUrl;
    private final String tags;
    private final String hiddenFieldName;
}
