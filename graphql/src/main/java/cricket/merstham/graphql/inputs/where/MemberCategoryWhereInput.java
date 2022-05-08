package cricket.merstham.graphql.inputs.where;

import cricket.merstham.graphql.entity.MemberCategoryEntity;
import cricket.merstham.graphql.inputs.filters.IntFilter;
import cricket.merstham.graphql.inputs.filters.StringFilter;
import org.springframework.data.web.ProjectedPayload;

import static java.util.Objects.nonNull;

@ProjectedPayload
public interface MemberCategoryWhereInput extends BaseWhere<MemberCategoryEntity> {
    IntFilter getId();

    StringFilter getKey();

    StringFilter getRegistrationCode();

    @Override
    default boolean matches(MemberCategoryEntity value) {
        boolean result = BaseWhere.super.matches(value);
        if (nonNull(getId())) {
            result &= getId().matches(value.getId());
        }
        if (nonNull(getKey())) {
            result &= getKey().matches(value.getKey());
        }
        if (nonNull(getRegistrationCode())) {
            result &= getRegistrationCode().matches(value.getRegistrationCode());
        }
        return result;
    }
}
