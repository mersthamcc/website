package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.DisplayService;
import cricket.merstham.shared.dto.DuckStatistics;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@RestController
public class DisplaysController {
    private final DisplayService displayService;

    public DisplaysController(DisplayService displayService) {
        this.displayService = displayService;
    }

    @GetMapping("/api/duck-statistics/{year}")
    public DuckStatistics getDuckStatsForYear(
            @PathVariable int year, @Param("length") Integer length) {
        var limit = isNull(length) ? 10 : length;
        return DuckStatistics.builder()
                .allDuckStatistics(displayService.getAllDuckStatistics(year, limit))
                .leagueDuckStatistics(displayService.getLeagueDuckStatistics(year, limit))
                .build();
    }
}
