package cricket.merstham.website.frontend.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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

@Getter
@Builder
@AllArgsConstructor
public class CognitoChallengeAuthentication implements Authentication {

    @Serial
    private static final long serialVersionUID = -8005659880146143771L;

    public enum Step {
        DEFAULT,
        SETUP_SOFTWARE_MFA,
        SETUP_SMS_MFA,
        SETUP_SMS_MFA_VERIFY
    }
    private static final ObjectMapper JSON = new JsonMapper();
    private final String sessionId;
    private final String email;
    private final ChallengeNameType challengeName;
    private final Map<String, String> challengeParameters;
    private final List<GrantedAuthority> grantedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_PRE_AUTH"));
    private final Object credentials;
    @Builder.Default
    private final Step step = Step.DEFAULT;

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
        return challengeParameters.get("USER_ID_FOR_SRP");
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return email;
    }

    public List<String> getAllowedMfaTypes() {
        if (challengeParameters.containsKey("MFAS_CAN_SETUP")) {
            try {
                return JSON.readValue(
                        challengeParameters.get("MFAS_CAN_SETUP"),
                        new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return List.of();
    }

    public String getMfaDestination() {
        return challengeParameters.get("CODE_DELIVERY_DESTINATION");
    }
}
