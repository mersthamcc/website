package cricket.merstham.shared.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize
public enum RegistrationAction implements Serializable {
    NEW("NEW"),
    RENEW("RENEW"),
    NONE("NONE");

    private String action;

    RegistrationAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
