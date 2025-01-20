package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.PasskitDeviceRegistrationEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PassKitDeviceRegistrationEntityRepository
        extends JpaRepository<PasskitDeviceRegistrationEntity, Integer>,
                PagingAndSortingRepository<PasskitDeviceRegistrationEntity, Integer> {
    Optional<PasskitDeviceRegistrationEntity> findFirstByDeviceLibraryIdentifier(
            @Size(max = 256) @NotNull String deviceLibraryIdentifier);
}
