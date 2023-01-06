package cricket.merstham.website.frontend.service;

import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.security.PendingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
public class CognitoService {

    private static final Logger LOG = LoggerFactory.getLogger(CognitoService.class);
    private final CognitoIdentityProviderClient client;
    private final String userPoolId;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    public CognitoService(
            @Value("${spring.security.oauth2.client.registration.login.region:#{null}}")
                    String region,
            @Value("${spring.security.oauth2.client.registration.login.user-pool-id:#{null}}")
                    String userPoolId,
            @Value("${spring.security.oauth2.client.registration.login.client-id:#{null}}")
                    String clientId,
            @Value("${spring.security.oauth2.client.registration.login.client-secret:#{null}}")
                    String clientSecret) {
        this.client = CognitoIdentityProviderClient.builder().region(Region.of(region)).build();
        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public CognitoAuthentication login(String username, String password) {
        try {
            var authParams =
                    Map.of(
                            "USERNAME", username,
                            "PASSWORD", password,
                            "SECRET_HASH", calculateSecretHash(clientId, clientSecret, username));
            var result =
                    client.adminInitiateAuth(
                            AdminInitiateAuthRequest.builder()
                                    .userPoolId(userPoolId)
                                    .authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                                    .authParameters(authParams)
                                    .clientId(clientId)
                                    .build());

            if (isNull(result.challengeName())) {
                return new CognitoAuthentication(
                        result.authenticationResult().accessToken(),
                        result.authenticationResult().refreshToken(),
                        result.authenticationResult().idToken(),
                        result.challengeNameAsString());
            }
        } catch (Exception ex) {
            LOG.error("Error calling Cognito", ex);
        }
        throw new BadCredentialsException("Invalid Credentials");
    }

    public CognitoAuthentication refresh(CognitoAuthentication authentication) {
        try {
            var username =
                    authentication
                            .getIdTokenJwt()
                            .getJWTClaimsSet()
                            .getStringClaim("cognito:username");
            var authParams =
                    Map.of(
                            "USERNAME", username,
                            "SECRET_HASH", calculateSecretHash(clientId, clientSecret, username),
                            "REFRESH_TOKEN", authentication.getRefreshToken());
            var result =
                    client.adminInitiateAuth(
                            AdminInitiateAuthRequest.builder()
                                    .userPoolId(userPoolId)
                                    .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                                    .authParameters(authParams)
                                    .clientId(clientId)
                                    .build());
            if (isNull(result.challengeName())) {
                return new CognitoAuthentication(
                        result.authenticationResult().accessToken(),
                        result.authenticationResult().refreshToken(),
                        result.authenticationResult().idToken(),
                        result.challengeNameAsString());
            }
        } catch (Exception ex) {
            LOG.error("Error calling Cognito", ex);
        }
        throw new BadCredentialsException("Invalid Credentials");
    }

    public PendingUser register(
            String email, String password, String givenName, String familyName) {
        var result =
                client.signUp(
                        SignUpRequest.builder()
                                .clientId(clientId)
                                .secretHash(calculateSecretHash(clientId, clientSecret, email))
                                .username(email)
                                .password(password)
                                .userAttributes(
                                        builder ->
                                                builder.name("given_name").value(givenName).build(),
                                        builder ->
                                                builder.name("family_name")
                                                        .value(familyName)
                                                        .build())
                                .build());

        return PendingUser.builder()
                .userId(result.userSub())
                .attributeName(result.codeDeliveryDetails().attributeName())
                .destination(result.codeDeliveryDetails().destination())
                .confirmationMedium(result.codeDeliveryDetails().deliveryMediumAsString())
                .build();
    }

    public boolean verify(String userId, String code) {
        try {
            var result =
                    client.confirmSignUp(
                            ConfirmSignUpRequest.builder()
                                    .username(userId)
                                    .clientId(clientId)
                                    .secretHash(calculateSecretHash(clientId, clientSecret, userId))
                                    .confirmationCode(code)
                                    .build());
            return result.sdkHttpResponse().isSuccessful();
        } catch (SdkException ex) {
            LOG.error("Error verifying user", ex);
            return false;
        }
    }

    private static String calculateSecretHash(
            String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey =
                new SecretKeySpec(
                        userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                        HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
