package cricket.merstham.website.frontend.service;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.shared.extensions.StringExtensions;
import cricket.merstham.website.frontend.model.UserSignUp;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.security.CognitoChallengeAuthentication;
import cricket.merstham.website.frontend.security.CognitoPasswordResetAuthentication;
import cricket.merstham.website.frontend.security.CognitoPendingUser;
import cricket.merstham.website.frontend.security.SealedString;
import cricket.merstham.website.frontend.security.exceptions.CognitoCodeException;
import cricket.merstham.website.frontend.security.exceptions.CognitoSessionExpiredException;
import cricket.merstham.website.frontend.security.exceptions.CognitoUserNotVerifiedException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import lombok.experimental.ExtensionMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AssociateSoftwareTokenRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CodeMismatchException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ExpiredCodeException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.LimitExceededException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListIdentityProvidersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.PasswordResetRequiredException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ProviderDescription;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResendConfirmationCodeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.TooManyRequestsException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static cricket.merstham.website.frontend.security.CognitoChallengeAuthentication.Step.SETUP_SOFTWARE_MFA;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenResponseType.SUCCESS;

@Service
@ExtensionMethod({StringExtensions.class})
public class CognitoService {

    private static final Logger LOG = LoggerFactory.getLogger(CognitoService.class);
    private static final String USERNAME = "USERNAME";
    private static final String SECRET_HASH = "SECRET_HASH"; // pragma: allowlist secret
    private static final String SMS_MFA_CODE = "SMS_MFA_CODE";
    private static final String SOFTWARE_TOKEN_MFA_CODE = "SOFTWARE_TOKEN_MFA_CODE";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String PASSWORD = "PASSWORD"; // pragma: allowlist secret
    private static final String COGNITO_USERNAME_CLAIM = "cognito:username";
    private static final String GIVEN_NAME_CLAIM = "given_name";
    private static final String FAMILY_NAME_CLAIM = "family_name";
    public static final String ANSWER = "ANSWER";

    private final String salt;
    private final CognitoIdentityProviderClient client;
    private final String userPoolId;
    private final String clientId;
    private final String clientSecret;
    private final String baseUri;
    private final String hostedUiUri;

    @Autowired
    public CognitoService(
            CognitoIdentityProviderClient client,
            @Value("${spring.security.oauth2.client.registration.login.user-pool-id:#{null}}")
                    String userPoolId,
            @Value("${spring.security.oauth2.client.registration.login.client-id:#{null}}")
                    String clientId,
            @Value("${spring.security.oauth2.client.registration.login.client-secret:#{null}}")
                    String clientSecret,
            @Value("${spring.security.oauth2.client.registration.login.session-salt:#{null}}")
                    String salt,
            @Value("${base-url:#{null}}") String baseUri,
            @Value("${spring.security.oauth2.client.registration.login.hosted-ui-uri:#{null}}")
                    String hostedUiUri) {
        this.client = client;
        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.salt = salt;
        this.baseUri = baseUri;
        this.hostedUiUri = hostedUiUri;
    }

    public Authentication login(String username, String password) {
        try {
            if (isNullOrEmpty(username) || isNullOrEmpty(password))
                throw new BadCredentialsException("Invalid Credentials");
            var authParams =
                    Map.of(
                            USERNAME, username,
                            PASSWORD, password,
                            SECRET_HASH, calculateSecretHash(clientId, clientSecret, username));
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
                        result.authenticationResult().idToken());
            } else {
                return CognitoChallengeAuthentication.builder()
                        .sessionId(result.session())
                        .email(username)
                        .challengeName(result.challengeName())
                        .challengeParameters(result.challengeParameters())
                        .credentials(
                                Map.of(
                                        PASSWORD,
                                        new SealedString(password, result.session(), salt)))
                        .build();
            }
        } catch (PasswordResetRequiredException ex) {
            LOG.warn("Password reset required for user {}, requesting code.", username);
            return resetPasswordRequest(username);
        } catch (UserNotConfirmedException ex) {
            throw new CognitoUserNotVerifiedException("User not confirmed", ex);
        } catch (CognitoIdentityProviderException ex) {
            LOG.error("Cognito Error", ex);
        }
        throw new BadCredentialsException("Invalid Credentials");
    }

    public CognitoAuthentication refresh(CognitoAuthentication authentication) {
        if (nonNull(authentication.getRefreshToken())) {
            try {
                var username =
                        authentication
                                .getIdTokenJwt()
                                .getJWTClaimsSet()
                                .getStringClaim(COGNITO_USERNAME_CLAIM);
                var authParams =
                        Map.of(
                                USERNAME, username,
                                SECRET_HASH, calculateSecretHash(clientId, clientSecret, username),
                                REFRESH_TOKEN, authentication.getRefreshToken());
                var result =
                        client.adminInitiateAuth(
                                AdminInitiateAuthRequest.builder()
                                        .userPoolId(userPoolId)
                                        .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                                        .authParameters(authParams)
                                        .clientId(clientId)
                                        .build());
                if (isNull(result.challengeName())) {
                    LOG.info("Token refresh response: {}", result);
                    return new CognitoAuthentication(
                            result.authenticationResult().accessToken(),
                            nonNull(result.authenticationResult().refreshToken())
                                    ? result.authenticationResult().refreshToken()
                                    : authentication.getRefreshToken(),
                            result.authenticationResult().idToken());
                }
            } catch (ParseException ex) {
                LOG.error("Error parsing JWT", ex);
            } catch (CognitoIdentityProviderException ex) {
                LOG.error("Cognito Error", ex);
            }
        }
        throw new BadCredentialsException("Invalid Credentials");
    }

    public boolean userExists(String username) {
        try {
            client.adminGetUser(
                    AdminGetUserRequest.builder()
                            .userPoolId(userPoolId)
                            .username(username)
                            .build());

            return true;
        } catch (UserNotFoundException ex) {
            return false;
        }
    }

    public CognitoPendingUser register(UserSignUp signUp) {
        var result =
                client.signUp(
                        SignUpRequest.builder()
                                .clientId(clientId)
                                .secretHash(
                                        calculateSecretHash(
                                                clientId, clientSecret, signUp.getEmail()))
                                .username(signUp.getEmail())
                                .password(signUp.getPassword())
                                .userAttributes(
                                        builder ->
                                                builder.name(GIVEN_NAME_CLAIM)
                                                        .value(signUp.getGivenName())
                                                        .build(),
                                        builder ->
                                                builder.name(FAMILY_NAME_CLAIM)
                                                        .value(signUp.getFamilyName())
                                                        .build())
                                .build());

        return CognitoPendingUser.builder()
                .userId(result.userSub())
                .attributeName(result.codeDeliveryDetails().attributeName())
                .destination(result.codeDeliveryDetails().destination())
                .confirmationMedium(result.codeDeliveryDetails().deliveryMediumAsString())
                .build();
    }

    public CognitoPendingUser resendVerificationCode(String username) {
        var request =
                ResendConfirmationCodeRequest.builder()
                        .clientId(clientId)
                        .username(username)
                        .secretHash(calculateSecretHash(clientId, clientSecret, username))
                        .build();

        var result = client.resendConfirmationCode(request);

        return CognitoPendingUser.builder()
                .userId(username)
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

    public Authentication getAppToken(CognitoChallengeAuthentication authentication) {
        var result =
                client.associateSoftwareToken(
                        AssociateSoftwareTokenRequest.builder()
                                .session(authentication.getSessionId())
                                .build());

        return CognitoChallengeAuthentication.builder()
                .sessionId(result.session())
                .email(authentication.getEmail())
                .challengeName(authentication.getChallengeName())
                .challengeParameters(authentication.getChallengeParameters())
                .credentials(
                        Map.of(
                                PASSWORD,
                                new SealedString(
                                        getPassword(authentication), result.session(), salt),
                                SOFTWARE_TOKEN_MFA_CODE,
                                result.secretCode()))
                .step(SETUP_SOFTWARE_MFA)
                .build();
    }

    public Authentication verifyAppSetup(
            CognitoChallengeAuthentication authentication, String code) {
        var verifySoftwareTokenResponse =
                client.verifySoftwareToken(
                        VerifySoftwareTokenRequest.builder()
                                .session(authentication.getSessionId())
                                .userCode(code)
                                .build());

        var userId = getUserId(authentication);
        if (verifySoftwareTokenResponse.status().equals(SUCCESS)) {
            var result =
                    client.adminRespondToAuthChallenge(
                            AdminRespondToAuthChallengeRequest.builder()
                                    .clientId(clientId)
                                    .challengeName(authentication.getChallengeName())
                                    .session(verifySoftwareTokenResponse.session())
                                    .userPoolId(userPoolId)
                                    .challengeResponses(
                                            Map.of(
                                                    USERNAME,
                                                    userId,
                                                    SECRET_HASH,
                                                    calculateSecretHash(
                                                            clientId, clientSecret, userId)))
                                    .build());

            return resultToAuthentication(authentication, result, null);
        }
        throw new BadCredentialsException("invalid_code");
    }

    public Authentication verifySoftwareMfa(
            CognitoChallengeAuthentication authentication, String code) {
        var userId = getUserId(authentication);

        try {
            var result =
                    client.adminRespondToAuthChallenge(
                            AdminRespondToAuthChallengeRequest.builder()
                                    .clientId(clientId)
                                    .challengeName(authentication.getChallengeName())
                                    .session(authentication.getSessionId())
                                    .userPoolId(userPoolId)
                                    .challengeResponses(
                                            Map.of(
                                                    USERNAME,
                                                    userId,
                                                    SOFTWARE_TOKEN_MFA_CODE,
                                                    code,
                                                    SECRET_HASH,
                                                    calculateSecretHash(
                                                            clientId, clientSecret, userId)))
                                    .build());

            return resultToAuthentication(authentication, result, userId);
        } catch (CodeMismatchException ex) {
            LOG.warn(ex.getMessage());
            return errorAuthentication(
                    CognitoChallengeAuthentication.Error.WRONG_CODE, authentication);
        } catch (ExpiredCodeException ex) {
            LOG.warn(ex.getMessage());
            return errorAuthentication(
                    CognitoChallengeAuthentication.Error.EXPIRED_CODE, authentication);
        } catch (NotAuthorizedException ex) {
            throw new CognitoSessionExpiredException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new CognitoCodeException(ex.getMessage(), ex);
        }
    }

    public Authentication verifySmsMfa(CognitoChallengeAuthentication authentication, String code) {
        var userId = getUserId(authentication);
        if (code.length() != 6 || !code.isNumeric()) {
            return errorAuthentication(
                    CognitoChallengeAuthentication.Error.WRONG_CODE, authentication);
        }
        try {
            var result =
                    respondToChallenge(
                            authentication,
                            Map.of(
                                    SMS_MFA_CODE,
                                    code,
                                    USERNAME,
                                    userId,
                                    SECRET_HASH,
                                    calculateSecretHash(clientId, clientSecret, userId)));

            return resultToAuthentication(authentication, result, userId);
        } catch (CodeMismatchException ex) {
            LOG.warn(ex.getMessage());
            return errorAuthentication(
                    CognitoChallengeAuthentication.Error.WRONG_CODE, authentication);
        } catch (ExpiredCodeException ex) {
            LOG.warn(ex.getMessage());
            return errorAuthentication(
                    CognitoChallengeAuthentication.Error.EXPIRED_CODE, authentication);
        } catch (NotAuthorizedException ex) {
            throw new CognitoSessionExpiredException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new CognitoCodeException(ex.getMessage(), ex);
        }
    }

    public Authentication selectMfaType(
            CognitoChallengeAuthentication authentication, String mfaType) {
        try {
            var userId = getUserId(authentication);
            var result =
                    respondToChallenge(
                            authentication,
                            Map.of(
                                    ANSWER,
                                    mfaType,
                                    USERNAME,
                                    userId,
                                    SECRET_HASH,
                                    calculateSecretHash(clientId, clientSecret, userId)));
            return resultToAuthentication(authentication, result, userId);
        } catch (NotAuthorizedException ex) {
            throw new CognitoSessionExpiredException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new CognitoCodeException(ex.getMessage(), ex);
        }
    }

    public Authentication setPhoneNumber(
            CognitoChallengeAuthentication authentication, String phoneNumber) {
        client.adminUpdateUserAttributes(
                AdminUpdateUserAttributesRequest.builder()
                        .username(getUserId(authentication))
                        .userPoolId(userPoolId)
                        .userAttributes(
                                List.of(
                                        AttributeType.builder()
                                                .name("phone_number")
                                                .value(phoneNumber)
                                                .build()))
                        .build());

        return login(authentication.getEmail(), getPassword(authentication));
    }

    public CognitoPasswordResetAuthentication resetPasswordRequest(String username) {
        var result =
                client.forgotPassword(
                        ForgotPasswordRequest.builder()
                                .clientId(clientId)
                                .username(username)
                                .build());

        return CognitoPasswordResetAuthentication.builder()
                .userId(username)
                .email(username)
                .credentials(result.codeDeliveryDetails())
                .build();
    }

    private AdminRespondToAuthChallengeResponse respondToChallenge(
            CognitoChallengeAuthentication authentication, Map<String, String> challengeResponses) {
        return client.adminRespondToAuthChallenge(
                AdminRespondToAuthChallengeRequest.builder()
                        .clientId(clientId)
                        .challengeName(authentication.getChallengeName())
                        .session(authentication.getSessionId())
                        .userPoolId(userPoolId)
                        .challengeResponses(challengeResponses)
                        .build());
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

    private String getUserId(CognitoChallengeAuthentication authentication) {
        return (String) authentication.getPrincipal();
    }

    private String getPassword(CognitoChallengeAuthentication authentication) {
        if (authentication.getCredentials() instanceof Map) {
            var credentials = (Map<String, Object>) authentication.getCredentials();
            if (credentials.containsKey(PASSWORD)) {
                var password = (SealedString) credentials.get(PASSWORD);
                return password.decrypt(authentication.getSessionId(), salt);
            }
        }
        return null;
    }

    private Authentication resultToAuthentication(
            CognitoChallengeAuthentication authentication,
            AdminRespondToAuthChallengeResponse result,
            String userId) {
        if (isNull(result.challengeName())) {
            return new CognitoAuthentication(
                    result.authenticationResult().accessToken(),
                    result.authenticationResult().refreshToken(),
                    result.authenticationResult().idToken());
        } else {
            return CognitoChallengeAuthentication.builder()
                    .challengeName(result.challengeName())
                    .challengeParameters(result.challengeParameters())
                    .sessionId(result.session())
                    .email(authentication.getEmail())
                    .userId(userId)
                    .credentials(
                            Map.of(
                                    PASSWORD,
                                    new SealedString(
                                            getPassword(authentication), result.session(), salt)))
                    .build();
        }
    }

    private Authentication errorAuthentication(
            CognitoChallengeAuthentication.Error error,
            CognitoChallengeAuthentication authentication) {
        return authentication.toBuilder().error(error).build();
    }

    public ForgotPasswordResponse forgotPassword(String email) {
        try {
            return client.forgotPassword(
                    ForgotPasswordRequest.builder()
                            .username(email)
                            .clientId(clientId)
                            .secretHash(calculateSecretHash(clientId, clientSecret, email))
                            .build());
        } catch (UserNotFoundException ex) {
            LOG.warn("Forgot Password - Username not found: {}", email);
            return null;
        }
    }

    public Optional<String> completeForgotPassword(String email, String code, String password) {
        try {
            client.confirmForgotPassword(
                    ConfirmForgotPasswordRequest.builder()
                            .clientId(clientId)
                            .username(email)
                            .confirmationCode(code)
                            .password(password)
                            .secretHash(calculateSecretHash(clientId, clientSecret, email))
                            .build());
            return Optional.empty();
        } catch (CodeMismatchException | UserNotFoundException ex) {
            return Optional.of("forgot-password.errors.incorrect-code");
        } catch (ExpiredCodeException ex) {
            return Optional.of("forgot-password.errors.code-expired");
        } catch (InvalidPasswordException ex) {
            return Optional.of("forgot-password.errors.password-not-valid");
        } catch (TooManyRequestsException | LimitExceededException ex) {
            return Optional.of("forgot-password.errors.too-many-attempts");
        } catch (Exception ex) {
            LOG.error("Unexpected error resetting password", ex);
            return Optional.of("forgot-password.errors.unknown");
        }
    }

    public List<String> getProviders() {
        return client
                .listIdentityProviders(
                        ListIdentityProvidersRequest.builder()
                                .maxResults(10)
                                .userPoolId(userPoolId)
                                .build())
                .providers()
                .stream()
                .map(ProviderDescription::providerName)
                .toList();
    }

    public String getProviderLoginUrl(String provider, String state) {
        var uri =
                UriBuilder.fromUri(hostedUiUri)
                        .path("/oauth2/authorize")
                        .queryParam("client_id", clientId)
                        .queryParam("response_type", "code")
                        .queryParam("redirect_uri", constructRedirectUrl())
                        .queryParam("identity_provider", provider)
                        .queryParam("scope", "email openid phone profile")
                        .queryParam("nonce", UUID.randomUUID())
                        .queryParam("state", state)
                        .build();

        return uri.toString();
    }

    public CognitoAuthentication authenticateWithCode(String code) {
        try {
            Client httpClient = ClientBuilder.newBuilder().build();
            var uri = UriBuilder.fromUri(hostedUiUri).path("/oauth2/token").build();
            var webTarget = httpClient.target(uri);
            var authHeader =
                    "Basic "
                            + Base64.getEncoder()
                                    .encodeToString(
                                            format("{0}:{1}", clientId, clientSecret)
                                                    .getBytes(StandardCharsets.UTF_8));
            var invocation =
                    webTarget
                            .request()
                            .accept(MediaType.WILDCARD_TYPE)
                            .header(AUTHORIZATION, authHeader)
                            .buildPost(
                                    Entity.form(
                                            new Form()
                                                    .param("grant_type", "authorization_code")
                                                    .param("client_id", "id")
                                                    .param("redirect_uri", constructRedirectUrl())
                                                    .param("code", code)));
            var response = invocation.invoke(JsonNode.class);

            return new CognitoAuthentication(
                    response.get("access_token").asText(),
                    response.get("refresh_token").asText(),
                    response.get("id_token").asText());
        } catch (Exception ex) {
            LOG.error("Error getting tokens", ex);
            return null;
        }
    }

    private String constructRedirectUrl() {
        return UriBuilder.fromUri(baseUri).path("/login/code").build().toString();
    }
}
