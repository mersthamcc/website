package cricket.merstham.website.frontend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class CognitoChallengeAuthentication implements Authentication {

    @Serial private static final long serialVersionUID = -8005659880146143771L;

    public enum Step {
        DEFAULT,
        SETUP_SOFTWARE_MFA,
        SETUP_SMS_MFA,
        SETUP_SMS_MFA_VERIFY,
        VERIFY_SOFTWARE_MFA,
        VERIFY_SMS_MFA
    }

    @AllArgsConstructor
    @Getter
    public enum Error {
        WRONG_CODE("wrong_code"),
        EXPIRED_CODE("expired_code");

        private String code;
    }

    private static final ObjectMapper JSON = new JsonMapper();
    private final String sessionId;
    private final String email;
    private final ChallengeNameType challengeName;
    private final Map<String, String> challengeParameters;

    private final String userId;
    private final List<GrantedAuthority> grantedAuthorities =
            List.of(new SimpleGrantedAuthority("ROLE_PRE_AUTH"));
    private final Object credentials;

    private final Error error;
    @Builder.Default private final Step step = Step.DEFAULT;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return challengeParameters;
    }

    @Override
    public Object getPrincipal() {
        return isNull(userId) ? challengeParameters.get("USER_ID_FOR_SRP") : userId;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

    @Override
    public String getName() {
        return email;
    }

    public String getAllowedMfaTypes() {
        if (challengeParameters.containsKey("MFAS_CAN_SETUP")) {
            return challengeParameters.get("MFAS_CAN_SETUP");
        }
        if (challengeParameters.containsKey("MFAS_CAN_CHOOSE")) {
            return challengeParameters.get("MFAS_CAN_CHOOSE");
        }
        return "[]";
    }

    public boolean isMfaSetup() {
        return challengeParameters.containsKey("MFAS_CAN_SETUP");
    }

    public String getMfaDestination() {
        return challengeParameters.get("CODE_DELIVERY_DESTINATION");
    }
}
