package cricket.merstham.shared.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DuckStatistics {
    private final List<AllDuckStatistic> allDuckStatistics;
    private final List<LeagueDuckStatistic> leagueDuckStatistics;
    private final List<AllDuckTakersStatistic> allDuckTakerStatistics;
    private final List<LeagueDuckTakersStatistic> leagueDuckTakerStatistics;
}
