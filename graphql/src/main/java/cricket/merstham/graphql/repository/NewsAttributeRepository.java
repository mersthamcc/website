package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.NewsAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface NewsAttributeRepository
        extends CrudRepository<NewsAttributeEntity, Integer>,
                JpaRepository<NewsAttributeEntity, Integer> {}
