package cricket.merstham.graphql.services;

import cricket.merstham.graphql.repository.AllDuckStatisticRepository;
import cricket.merstham.graphql.repository.LeagueDuckStatisticRepository;
import cricket.merstham.shared.dto.AllDuckStatistic;
import cricket.merstham.shared.dto.LeagueDuckStatistic;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisplayService {
    private final AllDuckStatisticRepository allDuckStatisticRepository;
    private final LeagueDuckStatisticRepository leagueDuckStatisticRepository;
    private final ModelMapper modelMapper;

    public DisplayService(
            AllDuckStatisticRepository allDuckStatisticRepository,
            LeagueDuckStatisticRepository leagueDuckStatisticRepository,
            ModelMapper modelMapper) {
        this.allDuckStatisticRepository = allDuckStatisticRepository;
        this.leagueDuckStatisticRepository = leagueDuckStatisticRepository;
        this.modelMapper = modelMapper;
    }

    @PreAuthorize("hasAuthority('TRUSTED_CLIENT')")
    public List<AllDuckStatistic> getAllDuckStatistics(int year, int length) {
        return allDuckStatisticRepository.getStatsForYear(year, length).stream()
                .map((element) -> modelMapper.map(element, AllDuckStatistic.class))
                .toList();
    }

    @PreAuthorize("hasAuthority('TRUSTED_CLIENT')")
    public List<LeagueDuckStatistic> getLeagueDuckStatistics(int year, int length) {
        return leagueDuckStatisticRepository.getStatsForYear(year, length).stream()
                .map((element) -> modelMapper.map(element, LeagueDuckStatistic.class))
                .toList();
    }
}
