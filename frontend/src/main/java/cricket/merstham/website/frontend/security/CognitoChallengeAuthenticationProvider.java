package cricket.merstham.website.frontend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CognitoChallengeAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CognitoChallengeAuthenticationProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var challenge = ((CognitoChallengeAuthentication) authentication).getChallengeName();
        switch(challenge) {
            case SMS_MFA:
                LOG.info("SMS MFA challenge issued");
                break;
            case SELECT_MFA_TYPE:
                LOG.info("Select MFA type challenge issued");
                break;
            case MFA_SETUP:
                LOG.info("MFA setup challenge issued");
                break;
            case NEW_PASSWORD_REQUIRED:
                LOG.info("New password challenge issued");
                break;
            case CUSTOM_CHALLENGE:
                LOG.info("Custom challenge issued");
                break;
            default:
                LOG.info("Unsupported challenge issued");
                break;
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CognitoChallengeAuthentication.class);
    }
}
