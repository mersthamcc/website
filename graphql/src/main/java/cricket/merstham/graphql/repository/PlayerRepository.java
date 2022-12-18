package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository
        extends CrudRepository<PlayerEntity, Integer>, JpaRepository<PlayerEntity, Integer> {}
