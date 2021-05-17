package cricket.merstham.website.frontend.model;

import java.io.Serializable;
import java.util.UUID;


public class RegistrationBasket implements Serializable {
    private String id;

    public RegistrationBasket() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public RegistrationBasket setId(String id) {
        this.id = id;
        return this;
    }
}
