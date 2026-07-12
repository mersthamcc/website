package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.LiveStreamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface LiveStreamRepository
        extends JpaRepository<LiveStreamEntity, Integer>,
                PagingAndSortingRepository<LiveStreamEntity, Integer> {
    Optional<LiveStreamEntity> getLiveStreamEntityByYoutubeId(String youtubeId);
}
