package cricket.merstham.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberCategoryFormSection implements Serializable {
    @Serial private static final long serialVersionUID = 8883412587082409200L;

    private Integer sortOrder;
    private Boolean showOnRegistration = false;
    private MemberCategory category;
    private MemberFormSection section;
}
