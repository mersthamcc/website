package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.ContactCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ContactCategoryEntityRepository
        extends JpaRepository<ContactCategoryEntity, Integer>,
                PagingAndSortingRepository<ContactCategoryEntity, Integer> {}
