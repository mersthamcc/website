package cricket.merstham.graphql.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final JdbcTemplate template;

    public ReportService(JdbcTemplate template) {
        this.template = template;
    }

    public List<Map<String, Object>> getReport() {
        return template.queryForList("");
    }
}
