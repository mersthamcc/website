package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.CouponEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CouponEntityRepository
        extends JpaRepository<CouponEntity, Integer>,
                PagingAndSortingRepository<CouponEntity, Integer> {
    List<CouponEntity> findAllByOwnerUserId(@Size(max = 64) @NotNull String ownerUserId);

    CouponEntity findFirstByCode(String code);
}
