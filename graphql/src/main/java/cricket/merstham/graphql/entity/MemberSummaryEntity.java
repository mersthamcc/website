package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.jpa.JpaJsonbConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Mapping for DB view */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "member_summary")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSummaryEntity {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "owner_user_id", length = Integer.MAX_VALUE)
    private String ownerUserId;

    @Column(name = "familyname", length = Integer.MAX_VALUE)
    private String familyName;

    @Column(name = "givenname", length = Integer.MAX_VALUE)
    private String givenName;

    @Column(name = "first_registration_date")
    private Instant firstRegistrationDate;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "agegroup", length = Integer.MAX_VALUE)
    private String ageGroup;

    @Column(name = "gender", length = Integer.MAX_VALUE)
    private String gender;

    @Column(name = "most_recent_subscription")
    private Integer mostRecentSubscription;

    @Column(name = "last_subs_date")
    private LocalDate lastSubsDate;

    @Column(name = "last_subs_price", precision = 10, scale = 2)
    private BigDecimal lastSubsPrice;

    @Size(max = 64)
    @Column(name = "last_subs_category", length = 64)
    private String lastSubsCategory;

    @Column(name = "received")
    private BigDecimal received;

    @Column(name = "payment_types", length = Integer.MAX_VALUE)
    private String paymentTypes;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "declarations")
    @Convert(converter = JpaJsonbConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode declarations;

    @Column(name = "identifiers")
    @Convert(converter = JpaJsonbConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode identifiers;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "apple_pass_serial_number", unique = true)
    private String applePassSerial;
}
