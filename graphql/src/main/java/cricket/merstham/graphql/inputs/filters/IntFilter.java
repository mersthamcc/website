package cricket.merstham.graphql.inputs.filters;

import org.springframework.data.web.ProjectedPayload;

import static java.util.Objects.nonNull;

@ProjectedPayload
public interface IntFilter extends BaseFilter<Integer> {
    Integer getLt();

    Integer getLte();

    Integer getGt();

    Integer getGte();

    @Override
    default boolean matches(Integer value) {
        boolean result = BaseFilter.super.matches(value);
        if (nonNull(getLt())) {
            result &= (value != null && value < getLt());
        }
        if (nonNull(getLte())) {
            result &= (value != null && value <= getLte());
        }
        if (nonNull(getGt())) {
            result &= (value != null && value > getGt());
        }
        if (nonNull(getGte())) {
            result &= (value != null && value >= getGte());
        }
        return result;
    }
}
