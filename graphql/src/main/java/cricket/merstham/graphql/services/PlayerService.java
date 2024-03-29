package cricket.merstham.graphql.services;

import cricket.merstham.graphql.repository.PlayerRepository;
import cricket.merstham.graphql.repository.PlayerSummaryRepository;
import cricket.merstham.shared.dto.Player;
import cricket.merstham.shared.dto.PlayerSummary;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository repository;
    private final PlayerSummaryRepository summaryRepository;
    private final ModelMapper mapper;

    @Autowired
    public PlayerService(
            PlayerRepository repository,
            PlayerSummaryRepository summaryRepository,
            ModelMapper mapper) {
        this.repository = repository;
        this.summaryRepository = summaryRepository;
        this.mapper = mapper;
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public List<Player> getPlayers() {
        return repository.findAll().stream()
                .map(p -> mapper.map(p, Player.class))
                .sorted(Comparator.comparing(Player::getName))
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public PlayerSummary getPlayer(int id) {
        return mapper.map(summaryRepository.findById(id), PlayerSummary.class);
    }
}
