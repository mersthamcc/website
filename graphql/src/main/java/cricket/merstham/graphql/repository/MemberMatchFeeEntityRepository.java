package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberMatchFeePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MemberMatchFeeEntityRepository
        extends JpaRepository<MemberMatchFeePaymentEntity, Integer>,
                PagingAndSortingRepository<MemberMatchFeePaymentEntity, Integer>,
                JpaSpecificationExecutor<MemberMatchFeePaymentEntity> {}
