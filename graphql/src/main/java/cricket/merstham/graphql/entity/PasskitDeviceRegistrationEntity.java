package cricket.merstham.graphql.entity;

import com.google.common.base.Strings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "passkit_device_registration",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "passkit_device_registration_device_library_identifier_key",
                    columnNames = {"device_library_identifier"})
        })
public class PasskitDeviceRegistrationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 256)
    @NotNull
    @Column(name = "device_library_identifier", nullable = false, length = 256)
    private String deviceLibraryIdentifier;

    @NotNull
    @Column(name = "push_token", nullable = false, length = Integer.MAX_VALUE)
    private String pushToken;

    @ManyToMany
    @JoinTable(
            name = "passkit_device_member_link",
            joinColumns = @JoinColumn(name = "device_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id"))
    private Set<MemberEntity> members = new LinkedHashSet<>();

    @Transient
    public boolean isValid() {
        return !(Strings.isNullOrEmpty(deviceLibraryIdentifier)
                || Strings.isNullOrEmpty(pushToken));
    }
}
