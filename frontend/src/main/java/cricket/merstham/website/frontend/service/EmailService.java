package cricket.merstham.website.frontend.service;

import cricket.merstham.website.frontend.configuration.ClubConfiguration;
import cricket.merstham.website.frontend.configuration.MailConfiguration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private final FreeMarkerConfigurer freemarkerConfigurer;
    private final SesV2Client client;
    private final String fromAddress;
    private final String resourcePrefix;
    private final ClubConfiguration clubConfiguration;

    @Autowired
    public EmailService(
            FreeMarkerConfigurer freemarkerConfigurer,
            @Value("${resources.base-url}") String resourcePrefix,
            ClubConfiguration clubConfiguration,
            MailConfiguration mailConfiguration) {
        this.freemarkerConfigurer = freemarkerConfigurer;
        this.client =
                SesV2Client.builder()
                        .region(Region.of(mailConfiguration.getRegion()))
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();
        this.fromAddress = mailConfiguration.getFromAddress();
        this.resourcePrefix = resourcePrefix;
        this.clubConfiguration = clubConfiguration;
    }

    public void sendEmail(
            String to,
            List<String> cc,
            List<String> bcc,
            String subject,
            String template,
            Map<String, Object> model) {
        try {
            var body = render(getTemplate(template), model);
            var result =
                    client.sendEmail(
                            SendEmailRequest.builder()
                                    .fromEmailAddress(fromAddress)
                                    .destination(
                                            builder ->
                                                    builder.toAddresses(to)
                                                            .ccAddresses(cc)
                                                            .bccAddresses(bcc))
                                    .content(
                                            builder ->
                                                    builder.simple(
                                                            m ->
                                                                    m.subject(s -> s.data(subject))
                                                                            .body(
                                                                                    b ->
                                                                                            b.html(
                                                                                                    h ->
                                                                                                            h
                                                                                                                    .data(
                                                                                                                            body)))))
                                    .build());

            LOG.info("E-mail sent with ID {}", result.messageId());
        } catch (IOException | TemplateException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String render(Template template, Map<String, Object> model)
            throws TemplateException, IOException {
        Map<String, Object> templateModel = new HashMap<>(model);
        templateModel.put("resourcePrefix", resourcePrefix);
        templateModel.put("config", clubConfiguration);
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, templateModel);
    }

    private Template getTemplate(String template) throws IOException {
        return freemarkerConfigurer.getConfiguration().getTemplate(template);
    }
}
