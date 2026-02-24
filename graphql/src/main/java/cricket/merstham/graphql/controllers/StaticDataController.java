package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.StaticDataService;
import cricket.merstham.shared.dto.StaticData;
import cricket.merstham.shared.dto.Totals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class StaticDataController {

    private final StaticDataService service;

    @Autowired
    public StaticDataController(StaticDataService service) {
        this.service = service;
    }

    @QueryMapping
    public Totals staticDataTotals(@Argument("searchString") String searchString) {
        return service.getStaticDataTotals();
    }

    @QueryMapping
    public StaticData staticDataById(@Argument("id") int id) {
        return service.getStaticDataById(id);
    }

    @QueryMapping
    public StaticData staticDataByPath(@Argument("path") String path) {
        return service.getStaticDataByPath(path);
    }

    @QueryMapping
    public List<StaticData> staticData(
            @Argument("start") int start,
            @Argument("length") int length,
            @Argument("searchString") String searchString,
            Principal principal) {
        return service.getAdminEntryList(start, length, searchString);
    }

    @MutationMapping
    public StaticData saveStaticData(@Argument("data") StaticData data) {
        return service.save(data);
    }

    @MutationMapping
    public StaticData deleteStaticData(@Argument("id") int id) {
        return service.delete(id);
    }
}
