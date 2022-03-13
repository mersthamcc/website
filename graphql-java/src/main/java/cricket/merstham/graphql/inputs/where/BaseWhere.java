package cricket.merstham.graphql.inputs.where;

import cricket.merstham.graphql.inputs.Filter;

import java.util.List;

import static java.util.Objects.nonNull;

public interface BaseWhere<T> extends Filter<T> {
    List<BaseWhere<T>> and();
    List<BaseWhere<T>> or();
    BaseWhere<T> not();

    @Override
    default boolean matches(T value) {
        boolean result = true;
        if (nonNull(and())) {
            result &= and().stream()
                    .map(f -> Boolean.valueOf(f.matches(value)))
                    .allMatch(r -> r.booleanValue() == true);
        }
        if (nonNull(or())) {
            result &= or().stream()
                    .map(f -> Boolean.valueOf(f.matches(value)))
                    .anyMatch(r -> r.booleanValue() == true);
        }
        if (nonNull(not())) {
            result &= !not().matches(value);
        }
        return result;
    };
}
