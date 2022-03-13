package cricket.merstham.graphql.inputs.filters;


import org.springframework.data.web.ProjectedPayload;

import static java.util.Objects.nonNull;

@ProjectedPayload
public interface StringFilter extends BaseFilter<String> {
    String getContains();
    String getStartsWith();
    String getEndsWith();

    @Override
    default boolean matches(String value) {
        boolean result = BaseFilter.super.matches(value);
        if (nonNull(getContains())) {
            result &= (value != null && value.contains(getContains()));
        }
        if (nonNull(getStartsWith())) {
            result &= (value != null && value.startsWith(getStartsWith()));
        }
        if (nonNull(getEndsWith())) {
            result &= (value != null && value.endsWith(getEndsWith()));
        }
        return result;
    }
}
