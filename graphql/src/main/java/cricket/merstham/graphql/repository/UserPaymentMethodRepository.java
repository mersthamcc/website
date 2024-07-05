package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.UserPaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserPaymentMethodRepository
        extends CrudRepository<UserPaymentMethodEntity, Integer>,
                JpaRepository<UserPaymentMethodEntity, Integer> {

    List<UserPaymentMethodEntity> findAllByUserId(String userId);
}
