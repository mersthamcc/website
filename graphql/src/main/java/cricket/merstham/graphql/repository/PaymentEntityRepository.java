package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentEntityRepository extends JpaRepository<PaymentEntity, Integer> {

    List<PaymentEntity>
            findPaymentEntitiesByReconciledIsFalseAndCollectedIsTrueAndAccountingErrorIsNull();

    Optional<PaymentEntity> findByTypeAndReference(String type, String reference);

    List<PaymentEntity> findByAccountingId(String id);

    List<PaymentEntity> findByTypeAndReferenceIn(String type, List<String> references);
}
