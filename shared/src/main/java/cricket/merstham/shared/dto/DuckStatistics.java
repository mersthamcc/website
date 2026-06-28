package cricket.merstham.shared.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DuckStatistics {
    private final List<AllDuckStatistic> allDuckStatistics;
    private final List<LeagueDuckStatistic> leagueDuckStatistics;
}
