package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class MemberSummaryAttributeEntityId implements Serializable {
    private static final long serialVersionUID = 7081632120247243132L;

    @Column(name = "member_id")
    private Long memberId;

    @Size(max = 64)
    @Column(name = "key", length = 64)
    private String key;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MemberSummaryAttributeEntityId entity = (MemberSummaryAttributeEntityId) o;
        return Objects.equals(this.key, entity.key)
                && Objects.equals(this.memberId, entity.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, memberId);
    }
}
