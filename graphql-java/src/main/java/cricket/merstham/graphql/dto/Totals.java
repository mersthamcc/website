package cricket.merstham.graphql.dto;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Totals implements Serializable {
    private static final long serialVersionUID = -3327616653096216319L;

    private long totalRecords;
    private long totalMatching;
}
