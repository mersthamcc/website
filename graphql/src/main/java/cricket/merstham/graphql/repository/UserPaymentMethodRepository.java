package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.UserPaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserPaymentMethodRepository
        extends CrudRepository<UserPaymentMethodEntity, Integer>,
                JpaRepository<UserPaymentMethodEntity, Integer> {

    List<UserPaymentMethodEntity> findAllByUserId(String userId);

    Optional<UserPaymentMethodEntity> findByUserIdAndProviderAndTypeAndMethodIdentifier(
            String userId, String provider, String type, String methodIdentifier);

    Optional<UserPaymentMethodEntity> findByProviderAndTypeAndMethodIdentifier(
            String provider, String type, String methodIdentifier);
}
