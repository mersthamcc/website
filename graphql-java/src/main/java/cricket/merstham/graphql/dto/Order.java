package cricket.merstham.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {
    private Integer id;
    private String uuid;
    private LocalDate createDate;
    private String accountingId;
    private String ownerUserId;
    private List<Payment> payment = new ArrayList<>();
    private List<MemberSubscription> subscription;
}
