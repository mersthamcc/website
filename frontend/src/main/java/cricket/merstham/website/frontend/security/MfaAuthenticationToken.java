package cricket.merstham.website.frontend.security;

import org.springframework.security.core.token.Token;

public class MfaAuthenticationToken implements Token {
    @Override
    public String getKey() {
        return null;
    }

    @Override
    public long getKeyCreationTime() {
        return 0;
    }

    @Override
    public String getExtendedInformation() {
        return null;
    }
}
