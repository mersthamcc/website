package cricket.merstham.graphql.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.graphql.entity.MemberSummaryEntity;
import cricket.merstham.graphql.repository.MemberSummaryRepository;
import cricket.merstham.shared.dto.MemberSummary;
import io.micrometer.core.annotation.Timed;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
public class SafeGuardingService {
    private static final Logger LOG = LogManager.getLogger(SafeGuardingService.class);
    private final MemberSummaryRepository memberSummaryRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    private final String safeguardingExportUrl;

    public SafeGuardingService(
            MemberSummaryRepository memberSummaryRepository,
            ModelMapper modelMapper,
            @Named("aws-signing-api-client") RestTemplate restTemplate,
            @Value("${configuration.safeguarding.export-url}") String safeguardingExportUrl) {
        this.memberSummaryRepository = memberSummaryRepository;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
        this.safeguardingExportUrl = safeguardingExportUrl;
    }

    @Scheduled(
            cron = "${configuration.safeguarding.export-cron}",
            zone = "${configuration.scheduler-zone}")
    @Timed(
            value = "playcricket.teams.refresh",
            description = "Time taken to process teams from PlayCricket")
    public void safeGuardExport() {
        LOG.info("SafeGuarding Export Started");

        var members =
                memberSummaryRepository.findAllByMostRecentSubscriptionGreaterThanEqual(
                        LocalDate.now().getYear() - 1);
        exportMembers(members);
        LOG.info("Safe Guarding Export Finished");
    }

    private void exportMembers(List<MemberSummaryEntity> members) {
        var request =
                ExportRequest.builder()
                        .members(
                                members.stream()
                                        .map(member -> modelMapper.map(member, MemberSummary.class))
                                        .toList())
                        .build();
        var response =
                restTemplate.postForEntity(safeguardingExportUrl, request, ExportResponse.class);

        LOG.info(
                "Safe Guarding Export Response: {} - {}",
                response.getStatusCode(),
                response.getBody().getMessage());
        if (response.getStatusCode() != HttpStatus.OK) {
            LOG.error(
                    "Safe Guarding Export Error: {} - {}",
                    response.getStatusCode(),
                    response.getBody().getErrorMessage());
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonSerialize
    @Accessors(chain = true)
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class ExportRequest {
        @JsonProperty private List<MemberSummary> members;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonSerialize
    @Accessors(chain = true)
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class ExportResponse {
        @JsonProperty private String message;
        @JsonProperty private String errorMessage;
    }
}
