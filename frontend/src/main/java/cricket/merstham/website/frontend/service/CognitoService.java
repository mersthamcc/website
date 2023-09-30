package cricket.merstham.website.frontend.service;

import cricket.merstham.shared.extensions.StringExtensions;
import cricket.merstham.website.frontend.model.UserSignUp;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.security.CognitoChallengeAuthentication;
import cricket.merstham.website.frontend.security.CognitoPasswordResetAuthentication;
import cricket.merstham.website.frontend.security.CognitoPendingUser;
import cricket.merstham.website.frontend.security.SealedString;
import cricket.merstham.website.frontend.security.exceptions.CognitoCodeException;
import cricket.merstham.website.frontend.security.exceptions.CognitoSessionExpiredException;
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
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ExpiredCodeException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.PasswordResetRequiredException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static cricket.merstham.website.frontend.security.CognitoChallengeAuthentication.Step.SETUP_SOFTWARE_MFA;
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
                    String salt) {
        this.client = client;
        this.userPoolId = userPoolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.salt = salt;
    }

    public Authentication login(String username, String password) {
        try {
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
}
