package cricket.merstham.graphql.services;

import cricket.merstham.graphql.configuration.BankDetails;
import cricket.merstham.graphql.configuration.MailTemplateConfiguration;
import cricket.merstham.graphql.entity.MemberSubscriptionEntity;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.Payment;
import cricket.merstham.shared.dto.User;
import io.rocketbase.mail.EmailTemplateBuilder;
import io.rocketbase.mail.TableLine;
import io.rocketbase.mail.model.HtmlTextEmail;
import io.rocketbase.mail.styling.Alignment;
import io.rocketbase.mail.table.ColumnConfig;
import io.rocketbase.mail.table.TableCellImageSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;

@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    public static final String CURRENCY_FORMAT = "'£'##.00";
    public static final String ORDER = "order";
    public static final String USER = "user";
    private final SesV2Client client;
    private final MailTemplateConfiguration configuration;
    private final MessageSource messageSource;
    private final BankDetails bankDetails;
    private final String baseUrl;
    private String resourcePrefix;

    @Autowired
    public EmailService(
            SesV2Client client,
            MailTemplateConfiguration configuration,
            MessageSource messageSource,
            BankDetails bankDetails,
            @Value("${configuration.base-url}") String baseUrl,
            @Value("${configuration.resources-base-url}") String resourcePrefix) {
        this.client = client;
        this.configuration = configuration;
        this.messageSource = messageSource;
        this.bankDetails = bankDetails;
        this.baseUrl = baseUrl;
        this.resourcePrefix = resourcePrefix;
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
            case MEMBERSHIP_CONFIRM -> membershipConfirmationEmail(model);
            case INCLUSIVE_KIT_ORDER -> inclusiveKitConfirmationEmail(model);
        };
    }

    private HtmlTextEmail inclusiveKitConfirmationEmail(Map<String, Object> model) {
        var user = (User) model.get(USER);
        var subscriptions = (List<MemberSubscriptionEntity>) model.get("subscriptions");
        var members = subscriptions.stream().map(m -> m.getMember().getFullName()).toList();
        var builder = configuration.getEmailBuilder();

        return builder.text(translation("email.INCLUSIVE_KIT_ORDER.title"))
                .h2()
                .and()
                .text(translation("email.salutation", user.getGivenName()))
                .and()
                .text(translation("email.INCLUSIVE_KIT_ORDER.paragraph1"))
                .and()
                .table(buildKitEligibleMemberTable(builder, members))
                .text(
                        translation(
                                "email.INCLUSIVE_KIT_ORDER.paragraph2",
                                configuration.getInclusiveKitPartner()))
                .and()
                .button(
                        translation("email.INCLUSIVE_KIT_ORDER.button"),
                        configuration.getInclusiveKitUrl())
                .and()
                .text(translation("email.INCLUSIVE_KIT_ORDER.paragraph3"))
                .and()
                .text(translation("email.INCLUSIVE_KIT_ORDER.sign-off"))
                .and()
                .text(configuration.getClubName())
                .bold()
                .and()
                .build();
    }

    private HtmlTextEmail membershipConfirmationEmail(Map<String, Object> model) {
        var user = (User) model.get(USER);
        var order = (Order) model.get(ORDER);
        var paymentType = (String) model.get("paymentType");
        var payments = order.getPayment();
        var builder = configuration.getEmailBuilder();

        builder =
                builder.text(translation("email.MEMBERSHIP_CONFIRM.title"))
                        .h2()
                        .and()
                        .text(translation("email.salutation", user.getGivenName()))
                        .and()
                        .text(
                                translation(
                                        "email.MEMBERSHIP_CONFIRM.paragraph1",
                                        LocalDate.now().getYear()))
                        .and()
                        .text(translation("email.MEMBERSHIP_CONFIRM.paragraph2"))
                        .and()
                        .table(buildSubscriptionsTable(builder, order))
                        .hr()
                        .and()
                        .text(translation("email.MEMBERSHIP_CONFIRM.pass-title"))
                        .h2()
                        .and()
                        .text(translation("email.MEMBERSHIP_CONFIRM.pass-paragraph1"))
                        .and()
                        .text(translation("email.MEMBERSHIP_CONFIRM.pass-paragraph2"))
                        .and()
                        .text(translation("email.MEMBERSHIP_CONFIRM.pass-paragraph3"))
                        .and()
                        .text(translation("email.MEMBERSHIP_CONFIRM.pass-ios-update"))
                        .bold()
                        .and()
                        .hr()
                        .and();

        if (!payments.isEmpty()) {
            builder =
                    builder.text(translation("email.MEMBERSHIP_CONFIRM.payments-header"))
                            .h2()
                            .and()
                            .text(translation("email.MEMBERSHIP_CONFIRM.payments-" + paymentType))
                            .and()
                            .table(getPaymentsTable(builder, payments));

        } else {
            switch (paymentType) {
                case "bank":
                    builder = addBankDetails(builder, order);
                    break;
                case "complementary":
                    break;
                default:
                    LOG.warn(
                            "Unexpected payment type specified while constructing confirmation e-mail: {}",
                            paymentType);
            }
        }

        builder.hr()
                .and()
                .text(translation("email.MEMBERSHIP_CONFIRM.spond-title"))
                .h2()
                .and()
                .text(translation("email.MEMBERSHIP_CONFIRM.spond-paragraph1"))
                .and()
                .text(translation("email.MEMBERSHIP_CONFIRM.spond-paragraph2"))
                .and()
                .image("https://resources.mersthamcc.co.uk/mcc/img/apps/apple-store-download.png")
                .linkUrl("https://apps.apple.com/gb/app/spond/id755596884")
                .alt("Download on the App Store")
                .width(120)
                .margin("15px")
                .and()
                .image("https://resources.mersthamcc.co.uk/mcc/img/apps/google-play-download.png")
                .linkUrl(
                        "https://play.google.com/store/apps/details?id=com.spond.spond&amp;hl=en_GB&amp;gl=US&amp;pli=1")
                .alt("Get it on Google Play")
                .width(120)
                .margin("15px")
                .and();

        return builder.text(translation("email.MEMBERSHIP_CONFIRM.sign-off"))
                .and()
                .text(configuration.getClubName())
                .bold()
                .and()
                .build();
    }

    private EmailTemplateBuilder.EmailTemplateConfigBuilder addBankDetails(
            EmailTemplateBuilder.EmailTemplateConfigBuilder builder, Order order) {
        return builder.text(
                        translation(
                                "email.MEMBERSHIP_CONFIRM.payments-bank", order.getWebReference()))
                .and()
                .attribute()
                .keyValue(
                        translation("email.MEMBERSHIP_CONFIRM.payments-bank-account-name"),
                        bankDetails.getAccountName())
                .keyValue(
                        translation("email.MEMBERSHIP_CONFIRM.payments-bank-account-number"),
                        bankDetails.getAccountNumber())
                .keyValue(
                        translation("email.MEMBERSHIP_CONFIRM.payments-bank-account-sort-code"),
                        bankDetails.getSortCode())
                .keyValue(
                        translation("email.MEMBERSHIP_CONFIRM.payments-bank-account-reference"),
                        order.getWebReference())
                .keyValue(
                        translation("email.MEMBERSHIP_CONFIRM.payments-bank-account-amount"),
                        NumberFormat.getCurrencyInstance(Locale.UK).format(order.getTotal()))
                .and();
    }

    private HtmlTextEmail mandateCancellationEmail(Map<String, Object> model) {
        var user = (User) model.get(USER);
        var order = (Order) model.get(ORDER);
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

    private TableLine buildKitEligibleMemberTable(
            EmailTemplateBuilder.EmailTemplateConfigBuilder builder, List<String> members) {
        return new TableLine() {
            @Override
            public List<ColumnConfig> getHeader() {
                return List.of();
            }

            @Override
            public List<ColumnConfig> getItem() {
                return List.of(new ColumnConfig());
            }

            @Override
            public List<ColumnConfig> getFooter() {
                return List.of();
            }

            @Override
            public List<List<Object>> getHeaderRows() {
                return List.of();
            }

            @Override
            public List<List<Object>> getItemRows() {
                return members.stream().map(List::<Object>of).toList();
            }

            @Override
            public List<List<Object>> getFooterRows() {
                return List.of();
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

    private TableLine buildSubscriptionsTable(
            EmailTemplateBuilder.EmailTemplateConfigBuilder builder, Order order) {
        return new TableLine() {
            @Override
            public List<ColumnConfig> getHeader() {
                return List.of(
                        new ColumnConfig(),
                        new ColumnConfig(),
                        new ColumnConfig().alignment(Alignment.RIGHT),
                        new ColumnConfig().alignment(Alignment.CENTER).colspan(2));
            }

            @Override
            public List<ColumnConfig> getItem() {
                return List.of(
                        new ColumnConfig(),
                        new ColumnConfig(),
                        new ColumnConfig().alignment(Alignment.RIGHT).numberFormat(CURRENCY_FORMAT),
                        new ColumnConfig().alignment(Alignment.CENTER),
                        new ColumnConfig().alignment(Alignment.CENTER));
            }

            @Override
            public List<ColumnConfig> getFooter() {
                return List.of(
                        new ColumnConfig(),
                        new ColumnConfig().alignment(Alignment.RIGHT).bold(),
                        new ColumnConfig().alignment(Alignment.RIGHT).numberFormat(CURRENCY_FORMAT),
                        new ColumnConfig(),
                        new ColumnConfig());
            }

            @Override
            public List<List<Object>> getHeaderRows() {
                return List.of(List.of("Name", "Category", "Price", "Digital Membership Card", ""));
            }

            @Override
            public List<List<Object>> getItemRows() {
                return order.getMemberSubscription().stream()
                        .map(
                                subscription -> {
                                    try {
                                        var appleCardLink =
                                                new TableCellImageSimple(
                                                        new URL(
                                                                        new URL(resourcePrefix),
                                                                        "/mcc/img/pass/apple-wallet.png")
                                                                .toString());
                                        appleCardLink.width(130);
                                        appleCardLink.alt(
                                                translation(
                                                        "email.MEMBERSHIP_CONFIRM.pass-download-apple"));
                                        appleCardLink.title(
                                                translation(
                                                        "email.MEMBERSHIP_CONFIRM.pass-download-apple"));
                                        appleCardLink.linkUrl(
                                                new URL(
                                                                new URL(baseUrl),
                                                                format(
                                                                        "/account/pass/{0}/apple",
                                                                        subscription
                                                                                .getMember()
                                                                                .getUuid()))
                                                        .toString());
                                        var googlePassLink =
                                                new TableCellImageSimple(
                                                        new URL(
                                                                        new URL(resourcePrefix),
                                                                        "/mcc/img/pass/google-add-wallet-badge.png")
                                                                .toString());
                                        googlePassLink.width(140);
                                        googlePassLink.alt(
                                                translation(
                                                        "email.MEMBERSHIP_CONFIRM.pass-download-google"));
                                        googlePassLink.title(
                                                translation(
                                                        "email.MEMBERSHIP_CONFIRM.pass-download-google"));
                                        googlePassLink.linkUrl(
                                                new URL(
                                                                new URL(baseUrl),
                                                                format(
                                                                        "/account/pass/{0}/google",
                                                                        subscription
                                                                                .getMember()
                                                                                .getUuid()))
                                                        .toString());

                                        return List.<Object>of(
                                                subscription.getMember().getFullName(),
                                                subscription.getPriceListItem().getDescription(),
                                                subscription.getPrice().doubleValue(),
                                                appleCardLink,
                                                googlePassLink);
                                    } catch (MalformedURLException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        .toList();
            }

            @Override
            public List<List<Object>> getFooterRows() {
                var result = new ArrayList<List<Object>>();
                if (nonNull(order.getDiscount())
                        && order.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                    result.add(
                            List.of(
                                    "",
                                    translation("email.MEMBERSHIP_CONFIRM.discount"),
                                    order.getDiscount().doubleValue(),
                                    "",
                                    ""));
                }
                result.add(
                        List.of(
                                "",
                                translation("email.MEMBERSHIP_CONFIRM.total"),
                                order.getTotal().doubleValue(),
                                "",
                                ""));
                return result;
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
                        new ColumnConfig()
                                .alignment(Alignment.RIGHT)
                                .numberFormat(CURRENCY_FORMAT));
            }

            @Override
            public List<ColumnConfig> getFooter() {
                return List.of(
                        new ColumnConfig(),
                        new ColumnConfig().alignment(Alignment.RIGHT).bold(),
                        new ColumnConfig()
                                .alignment(Alignment.RIGHT)
                                .numberFormat(CURRENCY_FORMAT));
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
        return translation(format("email.{0}.subject", template));
    }

    private List<String> getBccAddresses(MailTemplate template) {
        return switch (template) {
            case MANDATE_CANCEL, MEMBERSHIP_CONFIRM -> configuration.getMembershipBcc();
            case INCLUSIVE_KIT_ORDER -> List.of();
        };
    }

    private List<String> getCCAddresses(MailTemplate template) {
        return switch (template) {
            default -> List.of();
        };
    }

    public enum MailTemplate {
        MEMBERSHIP_CONFIRM,
        MANDATE_CANCEL,
        INCLUSIVE_KIT_ORDER
    }
}
