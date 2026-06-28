package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.DisplayService;
import cricket.merstham.shared.dto.DuckStatistics;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

@RestController
@CrossOrigin(origins = "*")
public class DisplaysController {
    private final DisplayService displayService;

    public DisplaysController(DisplayService displayService) {
        this.displayService = displayService;
    }

    @GetMapping("/api/duck-statistics/{year}")
    public ResponseEntity<DuckStatistics> getDuckStatsForYear(
            @PathVariable int year, @Param("length") Integer length) {
        var limit = isNull(length) ? 10 : length;
        return new ResponseEntity<>(
                DuckStatistics.builder()
                        .allDuckStatistics(displayService.getAllDuckStatistics(year, limit))
                        .leagueDuckStatistics(displayService.getLeagueDuckStatistics(year, limit))
                        .build(),
                HttpStatus.OK);
    }

    @RequestMapping(value = "/api/**", method = RequestMethod.OPTIONS)
    public ResponseEntity options() {
        return ResponseEntity.ok().build();
    }
}
