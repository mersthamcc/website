package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberAttendanceSummaryEntity;
import cricket.merstham.graphql.inputs.AttendanceFilterInput;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public interface MemberAttendanceSummaryRepository
        extends JpaRepository<MemberAttendanceSummaryEntity, Integer>,
                PagingAndSortingRepository<MemberAttendanceSummaryEntity, Integer>,
                JpaSpecificationExecutor<MemberAttendanceSummaryEntity> {

    default Specification<MemberAttendanceSummaryEntity> getMemberSpecification(
            AttendanceFilterInput filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(
                    criteriaBuilder.between(
                            root.get("time"),
                            filter.getFrom().atStartOfDay().toInstant(ZoneOffset.UTC),
                            filter.getTo().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC)));
            if (filter.getAgeGroup().isPresent()) {
                var in = criteriaBuilder.in(root.get("ageGroup"));
                filter.getAgeGroup()
                        .get()
                        .forEach(
                                c -> {
                                    in.value(c);
                                });
                predicates.add(in);
            }

            if (filter.getIncludeUnregistered().isPresent()
                    && !filter.getIncludeUnregistered().get()) {
                predicates.add(criteriaBuilder.isNotNull(root.get("memberId")));
            }

            if (filter.getMemberId().isPresent()) {
                predicates.add(
                        criteriaBuilder.equal(root.get("memberId"), filter.getMemberId().get()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
