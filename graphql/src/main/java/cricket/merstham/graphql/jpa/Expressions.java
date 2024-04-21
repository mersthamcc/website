package cricket.merstham.graphql.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

public class Expressions {
    public static <T> Expression<Boolean> jsonb_contains(
            Root<T> root, CriteriaBuilder criteriaBuilder, String field, String value) {
        return criteriaBuilder.function(
                "jsonb_contains",
                Boolean.class,
                root.get(field),
                criteriaBuilder.function(
                        "jsonb_build_array", Object.class, criteriaBuilder.literal(value)));
    }
}
