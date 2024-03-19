package cricket.merstham.graphql.entity;

import cricket.merstham.graphql.jpa.JpaJsonbToIntegerListConverter;
import cricket.merstham.graphql.jpa.JpaJsonbToStringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "member_filter")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFilterEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "categories")
    @Convert(converter = JpaJsonbToStringListConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> categories;

    @Column(name = "years_of_birth")
    @Convert(converter = JpaJsonbToIntegerListConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Integer> yearsOfBirth;

    @Column(name = "genders")
    @Convert(converter = JpaJsonbToStringListConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> genders;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass =
                o instanceof HibernateProxy
                        ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                        : o.getClass();
        Class<?> thisEffectiveClass =
                this instanceof HibernateProxy
                        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                        : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MemberFilterEntity that = (MemberFilterEntity) o;
        return getUserId() != null && Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this)
                        .getHibernateLazyInitializer()
                        .getPersistentClass()
                        .hashCode()
                : getClass().hashCode();
    }
}
