package cricket.merstham.graphql.inputs.filters;

import cricket.merstham.graphql.inputs.Filter;
import org.springframework.data.web.ProjectedPayload;

import java.util.List;

import static java.util.Objects.nonNull;

@ProjectedPayload
public interface BaseFilter<T> extends Filter<T> {

    T getEquals();

    List<T> getIn();

    List<T> getNotIn();

    BaseFilter<T> getNot();

    @Override
    default boolean matches(T value) {
        boolean result = true;
        if (nonNull(getEquals())) {
            result &= getEquals().equals(value);
        }
        if (nonNull(getIn())) {
            result &= getIn().contains(value);
        }
        if (nonNull(getNotIn())) {
            result &= !getNotIn().contains(value);
        }
        if (nonNull(getNot())) {
            result &= !getNot().matches(value);
        }
        return result;
    }
}
