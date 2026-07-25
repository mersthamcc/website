package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberMatchFeePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MemberMatchFeePaymentEntityRepository
        extends JpaRepository<MemberMatchFeePaymentEntity, String>,
                PagingAndSortingRepository<MemberMatchFeePaymentEntity, String>,
                JpaSpecificationExecutor<MemberMatchFeePaymentEntity> {}
