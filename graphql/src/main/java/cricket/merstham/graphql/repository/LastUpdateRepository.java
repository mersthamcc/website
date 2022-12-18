package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.LastUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface LastUpdateRepository
        extends CrudRepository<LastUpdateEntity, String>, JpaRepository<LastUpdateEntity, String> {}
