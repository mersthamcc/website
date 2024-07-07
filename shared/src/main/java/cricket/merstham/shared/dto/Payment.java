package cricket.merstham.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {
    @Serial private static final long serialVersionUID = -6204067832877516899L;

    private Integer id;
    private Order order;
    private String type;
    private String reference;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal processingFees;
    private String accountingId;
    private String feesAccountingId;
    private Boolean collected;
    private Boolean reconciled;
    private String status;
    private String link;
}
