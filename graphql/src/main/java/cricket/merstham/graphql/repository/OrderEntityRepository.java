package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderEntityRepository extends JpaRepository<OrderEntity, Integer> {
    List<OrderEntity> findByOwnerUserIdAllIgnoreCaseOrderByCreateDateAsc(
            @NonNull String ownerUserId);

    List<OrderEntity> findByCreateDateBetween(
            @NonNull LocalDate createDateStart, @NonNull LocalDate createDateEnd);

    List<OrderEntity> findOrderEntitiesByAccountingIdIsNull();

    Optional<OrderEntity> findOrderEntityByAccountingId(String accountingId);
}
