package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize
public enum RegistrationAction implements Serializable {
    NEW("NEW"),
    RENEW("RENEW");

    String action;

    RegistrationAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
