package cricket.merstham.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberFormSection implements Serializable {
    @Serial
    private static final long serialVersionUID = 2202185495038356659L;

    private Integer id;
    private String key;
    private List<MemberFormSectionAttribute> attribute;
}
