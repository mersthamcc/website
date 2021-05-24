package cricket.merstham.website.frontend.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import static java.lang.String.format;

public class Order implements Serializable {

    private static final long serialVersionUID = 20210524192800L;

    private int id;
    private UUID uuid;
    private BigDecimal total;

    public int getId() {
        return id;
    }

    public Order setId(int id) {
        this.id = id;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Order setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Order setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getWebReference() {
        return format("WEB-%1$6s", id).replace(' ', '0');
    }
}
