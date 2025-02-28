package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/** Mapping for DB view */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "member_summary_attribute")
public class MemberSummaryAttributeEntity {
    @EmbeddedId private MemberSummaryAttributeEntityId id;

    @Column(name = "value", length = Integer.MAX_VALUE)
    private String value;
}
