package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TeamRepository
        extends CrudRepository<TeamEntity, Integer>, JpaRepository<TeamEntity, Integer> {
    List<TeamEntity> findByStatusAllIgnoreCaseOrderBySortOrderAsc(@NonNull String status);

    TeamEntity findBySlugEqualsIgnoreCase(@NonNull String slug);
}
