package cricket.merstham.graphql.services;

import cricket.merstham.graphql.configuration.MailTemplateConfiguration;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.Payment;
import cricket.merstham.shared.dto.User;
import io.rocketbase.mail.EmailTemplateBuilder;
import io.rocketbase.mail.TableLine;
import io.rocketbase.mail.model.HtmlTextEmail;
import io.rocketbase.mail.styling.Alignment;
import io.rocketbase.mail.table.ColumnConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.text.MessageFormat.format;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private final SesV2Client client;
    private final MailTemplateConfiguration configuration;
    private final MessageSource messageSource;

    @Autowired
    public EmailService(
            SesV2Client client,
            MailTemplateConfiguration configuration,
            MessageSource messageSource) {
        this.client = client;
        this.configuration = configuration;
        this.messageSource = messageSource;
    }

    public void sendEmail(String to, MailTemplate template, Map<String, Object> model) {
        var body = buildEmail(template, model);
        var result =
                client.sendEmail(
                        SendEmailRequest.builder()
                                .fromEmailAddress(configuration.getFromAddress())
                                .destination(
                                        b ->
                                                b.toAddresses(to)
                                                        .ccAddresses(getCCAddresses(template))
                                                        .bccAddresses(getBccAddresses(template)))
                                .content(b -> getContent(b, getSubject(template), body))
                                .build());

        LOG.info("E-mail sent with ID {}", result.messageId());
    }

    private void getContent(EmailContent.Builder builder, String subject, HtmlTextEmail body) {
        builder.simple(
                m ->
                        m.subject(s -> s.data(subject))
                                .body(
                                        b ->
                                                b.html(h -> h.data(body.getHtml()))
                                                        .text(t -> t.data(body.getText()))));
    }

    private HtmlTextEmail buildEmail(MailTemplate template, Map<String, Object> model) {
        return switch (template) {
            case MANDATE_CANCEL -> mandateCancellationEmail(model);
            default -> throw new IllegalArgumentException(
                    format("Unsupported template value: {0}", template));
        };
    }

    private HtmlTextEmail mandateCancellationEmail(Map<String, Object> model) {
        var user = (User) model.get("user");
        var order = (Order) model.get("order");
        var payments = order.getPayment().stream().filter(p -> !p.getCollected()).toList();
        var builder = configuration.getEmailBuilder();
        return builder.text(translation("email.MANDATE_CANCEL.title"))
                .h2()
                .and()
                .text(translation("email.salutation", user.getGivenName()))
                .and()
                .text(translation("email.MANDATE_CANCEL.paragraph1", model.get("mandate")))
                .and()
                .text(translation("email.MANDATE_CANCEL.paragraph2", order.getWebReference()))
                .and()
                .table(getPaymentsTable(builder, payments))
                .text(translation("email.MANDATE_CANCEL.paragraph3"))
                .and()
                .text(translation("email.MANDATE_CANCEL.sign-off"))
                .and()
                .text(configuration.getClubName())
                .bold()
                .and()
                .build();
    }

    private String translation(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

    private TableLine getPaymentsTable(
            EmailTemplateBuilder.EmailTemplateConfigBuilder builder, List<Payment> payments) {
        return new TableLine() {
            @Override
            public List<ColumnConfig> getHeader() {
                return List.of();
            }

            @Override
            public List<ColumnConfig> getItem() {
                return List.of(
                        new ColumnConfig(),
                        new ColumnConfig(),
                        new ColumnConfig().alignment(Alignment.RIGHT).numberFormat("'£'##.00"));
            }

            @Override
            public List<ColumnConfig> getFooter() {
                return List.of(
                        new ColumnConfig(),
                        new ColumnConfig().alignment(Alignment.RIGHT).bold(),
                        new ColumnConfig().alignment(Alignment.RIGHT).numberFormat("'£'##.00"));
            }

            @Override
            public List<List<Object>> getHeaderRows() {
                return List.of(List.of("Date", "Reference", "Amount"));
            }

            @Override
            public List<List<Object>> getItemRows() {
                return payments.stream()
                        .map(
                                payment ->
                                        List.<Object>of(
                                                payment.getDate()
                                                        .format(
                                                                DateTimeFormatter.ofLocalizedDate(
                                                                        FormatStyle.SHORT)),
                                                payment.getReference(),
                                                payment.getAmount().doubleValue()))
                        .toList();
            }

            @Override
            public List<List<Object>> getFooterRows() {
                return List.of(
                        List.of(
                                "",
                                "Total",
                                payments.stream()
                                        .map(Payment::getAmount)
                                        .reduce(BigDecimal::add)
                                        .map(BigDecimal::doubleValue)
                                        .orElse(0.00)));
            }

            @Override
            public EmailTemplateBuilder.EmailTemplateConfigBuilder and() {
                return builder;
            }

            @Override
            public HtmlTextEmail build() {
                return builder.build();
            }
        };
    }

    private String getSubject(MailTemplate template) {
        return translation(format("email.{0}.subject", template.toString()));
    }

    private List<String> getBccAddresses(MailTemplate template) {
        return switch (template) {
            case MANDATE_CANCEL -> configuration.getMembershipBcc();
            default -> throw new IllegalArgumentException(
                    format("Unsupported template value: {0}", template));
        };
    }

    private List<String> getCCAddresses(MailTemplate template) {
        return switch (template) {
            case MANDATE_CANCEL -> List.of();
            default -> throw new IllegalArgumentException(
                    format("Unsupported template value: {0}", template));
        };
    }

    public enum MailTemplate {
        MEMBERSHIP_CONFIRM,
        MANDATE_CANCEL
    }
}
