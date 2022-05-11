package cricket.merstham.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsAttribute implements Serializable {
    private static final long serialVersionUID = -8246609901272196328L;

    private Integer id;
    private News news;
    private String name;
    private String value;
}
