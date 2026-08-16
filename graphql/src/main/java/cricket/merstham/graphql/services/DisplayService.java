package cricket.merstham.graphql.services;

import cricket.merstham.graphql.repository.AllDuckStatisticRepository;
import cricket.merstham.graphql.repository.AllDuckTakerStatisticRepository;
import cricket.merstham.graphql.repository.LeagueDuckStatisticRepository;
import cricket.merstham.graphql.repository.LeagueDuckTakersStatisticRepository;
import cricket.merstham.shared.dto.AllDuckStatistic;
import cricket.merstham.shared.dto.AllDuckTakersStatistic;
import cricket.merstham.shared.dto.LeagueDuckStatistic;
import cricket.merstham.shared.dto.LeagueDuckTakersStatistic;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisplayService {
    private final AllDuckStatisticRepository allDuckStatisticRepository;
    private final LeagueDuckStatisticRepository leagueDuckStatisticRepository;
    private final ModelMapper modelMapper;
    private final AllDuckTakerStatisticRepository allDuckTakerStatisticRepository;
    private final LeagueDuckTakersStatisticRepository leagueDuckTakersStatisticRepository;

    public DisplayService(
            AllDuckStatisticRepository allDuckStatisticRepository,
            LeagueDuckStatisticRepository leagueDuckStatisticRepository,
            ModelMapper modelMapper,
            AllDuckTakerStatisticRepository allDuckTakerStatisticRepository,
            LeagueDuckTakersStatisticRepository leagueDuckTakersStatisticRepository) {
        this.allDuckStatisticRepository = allDuckStatisticRepository;
        this.leagueDuckStatisticRepository = leagueDuckStatisticRepository;
        this.modelMapper = modelMapper;
        this.allDuckTakerStatisticRepository = allDuckTakerStatisticRepository;
        this.leagueDuckTakersStatisticRepository = leagueDuckTakersStatisticRepository;
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

    @PreAuthorize("hasAuthority('TRUSTED_CLIENT')")
    public List<AllDuckTakersStatistic> getAllDuckTakersStatistics(int year, int limit) {
        return allDuckTakerStatisticRepository.getStatsForYear(year, limit).stream()
                .map((element) -> modelMapper.map(element, AllDuckTakersStatistic.class))
                .toList();
    }

    @PreAuthorize("hasAuthority('TRUSTED_CLIENT')")
    public List<LeagueDuckTakersStatistic> getLeagueDuckTakersStatistics(int year, int limit) {
        return leagueDuckTakersStatisticRepository.getStatsForYear(year, limit).stream()
                .map((element) -> modelMapper.map(element, LeagueDuckTakersStatistic.class))
                .toList();
    }
}
