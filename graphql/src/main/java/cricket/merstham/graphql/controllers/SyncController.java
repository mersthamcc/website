package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.FixtureService;
import cricket.merstham.shared.dto.CalendarSyncResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
public class SyncController {

    private static final String HAS_SYSTEM_ROLE = "hasRole('ROLE_SYSTEM')";

    private final FixtureService fixtureService;

    @Autowired
    public SyncController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @MutationMapping
    @PreAuthorize(HAS_SYSTEM_ROLE)
    public List<CalendarSyncResult> calendarSync(@Argument LocalDate start) {
        return fixtureService.syncFixturesWithCalendar(start);
    }
}
