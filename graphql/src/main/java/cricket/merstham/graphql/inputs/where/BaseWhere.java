package cricket.merstham.graphql.inputs.where;

import cricket.merstham.graphql.inputs.Filter;
import org.springframework.data.web.ProjectedPayload;

import java.util.List;

import static java.util.Objects.nonNull;

@ProjectedPayload
public interface BaseWhere<T> extends Filter<T> {
    List<BaseWhere<T>> getAnd();

    List<BaseWhere<T>> getOr();

    BaseWhere<T> getNot();

    @Override
    default boolean matches(T value) {
        boolean result = true;
        if (nonNull(getAnd()) && !getAnd().isEmpty()) {
            result &=
                    getAnd().stream().map(f -> Boolean.valueOf(f.matches(value))).allMatch(r -> r);
        }
        if (nonNull(getOr()) && !getOr().isEmpty()) {
            result &= getOr().stream().map(f -> Boolean.valueOf(f.matches(value))).anyMatch(r -> r);
        }
        if (nonNull(getNot())) {
            result &= !getNot().matches(value);
        }
        return result;
    }
    ;
}
