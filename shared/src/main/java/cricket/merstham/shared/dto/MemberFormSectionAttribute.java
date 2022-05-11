package cricket.merstham.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormSectionAttribute implements Serializable {
    @Serial private static final long serialVersionUID = 3126990136870777138L;

    private Integer sortOrder;
    private Boolean mandatory = false;
    private MemberFormSection section;
    private AttributeDefinition definition;
}
