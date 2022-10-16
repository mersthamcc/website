package cricket.merstham.website.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class Order implements Serializable {

    @Serial private static final long serialVersionUID = 20210524192800L;

    private int id;
    private UUID uuid;
    private BigDecimal total;
    private Map<UUID, Subscription> subscriptions;

    public String getWebReference() {
        return format("WEB-%1$6s", id).replace(' ', '0');
    }
}
