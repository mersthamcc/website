package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.gocardless.GoCardlessClient;
import com.gocardless.resources.Mandate;
import com.gocardless.resources.RedirectFlow;
import com.gocardless.services.RedirectFlowService;
import cricket.merstham.website.frontend.exception.GraphException;
import cricket.merstham.website.frontend.model.MandatePresentation;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static cricket.merstham.website.frontend.service.payment.GoCardlessService.MANDATE;
import static java.text.MessageFormat.format;

@Service
public class MandateService {
    private static final Logger LOG = LoggerFactory.getLogger(MandateService.class);

    public static final String GOCARDLESS = "gocardless";
    private final GoCardlessClient client;
    private final MembershipService membershipService;
    @Getter private final String mandateDescription;

    @Autowired
    public MandateService(
            GoCardlessClient client,
            MembershipService membershipService,
            @Value("${payments.gocardless.mandate-description}") String mandateDescription) {
        this.client = client;
        this.membershipService = membershipService;
        this.mandateDescription = mandateDescription;
    }

    public List<MandatePresentation> getUserMandates(Principal principal) {
        var authentication = (CognitoAuthentication) principal;
        try {
            var paymentMethods =
                    membershipService.getUsersPaymentMethods(
                            authentication.getOidcUser().getSubject(),
                            authentication.getOAuth2AccessToken());

            return paymentMethods.stream()
                    .filter(
                            p ->
                                    p.getType().equals(MANDATE)
                                            && p.getStatus().equals("active")
                                            && p.getProvider().equals(GOCARDLESS))
                    .map(
                            p ->
                                    MandatePresentation.builder()
                                            .mandate(
                                                    client.mandates()
                                                            .get(p.getMethodIdentifier())
                                                            .execute())
                                            .build())
                    .filter(
                            mandate ->
                                    mandate.getMandate()
                                            .getStatus()
                                            .name()
                                            .equals(Mandate.Status.ACTIVE.name()))
                    .map(
                            m ->
                                    m.setCustomerBankAccount(
                                            client.customerBankAccounts()
                                                    .get(
                                                            m.getMandate()
                                                                    .getLinks()
                                                                    .getCustomerBankAccount())
                                                    .execute()))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public RedirectFlow createNewMandate(
            HttpServletRequest request, String redirectPath, String sessionToken) {
        var requestUri = URI.create(request.getRequestURL().toString());
        String redirectUri =
                format(
                        "{0}://{1}{2}",
                        requestUri.getScheme(), requestUri.getAuthority(), redirectPath);
        var user = ((CognitoAuthentication) request.getUserPrincipal()).getOidcUser();

        return client.redirectFlows()
                .create()
                .withDescription(getMandateDescription())
                .withIdempotencyKey(UUID.randomUUID().toString())
                .withSessionToken(sessionToken)
                .withSuccessRedirectUrl(redirectUri)
                .withPrefilledCustomerEmail(user.getEmail())
                .withPrefilledCustomerGivenName(user.getGivenName())
                .withPrefilledCustomerFamilyName(user.getFamilyName())
                .withScheme(RedirectFlowService.RedirectFlowCreateRequest.Scheme.BACS)
                .execute();
    }

    public Mandate completeRedirectFlow(
            String redirectFlowId, String sessionToken, OAuth2AccessToken accessToken) {
        var redirectFlow =
                client.redirectFlows()
                        .complete(redirectFlowId)
                        .withSessionToken(sessionToken)
                        .execute();

        var mandateId = redirectFlow.getLinks().getMandate();
        var mandate = client.mandates().get(mandateId).execute();
        LOG.info("Saving mandate {}", mandate.getId());
        try {
            membershipService.createUserPaymentMethod(
                    GOCARDLESS,
                    MANDATE,
                    mandate.getId(),
                    mandate.getLinks().getCustomer(),
                    "pending",
                    accessToken);
        } catch (GraphException e) {
            LOG.atError()
                    .setCause(e)
                    .log(
                            "Error saving mandate {} - '{}'",
                            mandate.getId(),
                            String.join(
                                    ", ", e.getErrors().stream().map(Error::getMessage).toList()),
                            e);
        } catch (Exception e) {
            LOG.atError()
                    .setCause(e)
                    .log("Unable to save payment method for mandate {}", mandate.getId());
        }
        return mandate;
    }
}
