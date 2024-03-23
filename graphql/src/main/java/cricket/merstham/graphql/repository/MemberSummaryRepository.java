package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberSummaryEntity;
import cricket.merstham.shared.dto.MemberFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface MemberSummaryRepository
        extends JpaRepository<MemberSummaryEntity, Integer>,
                PagingAndSortingRepository<MemberSummaryEntity, Integer>,
                JpaSpecificationExecutor<MemberSummaryEntity> {

    default Specification<MemberSummaryEntity> getMemberSpecification(MemberFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!filter.getCategories().isEmpty()) {
                var in = criteriaBuilder.in(root.get("lastSubsCategory"));
                filter.getCategories()
                        .forEach(
                                c -> {
                                    in.value(c);
                                });
                predicates.add(in);
            }

            if (!filter.getYearsOfBirth().isEmpty()) {
                List<Predicate> dobPredicates = new ArrayList<>();
                filter.getYearsOfBirth()
                        .forEach(
                                year -> {
                                    dobPredicates.add(
                                            criteriaBuilder.between(
                                                    root.get("dob"),
                                                    LocalDate.of(year - 1, 9, 1),
                                                    LocalDate.of(year, 8, 31)));
                                });
                predicates.add(criteriaBuilder.or(dobPredicates.toArray(new Predicate[0])));
            }

            if (!filter.getGenders().isEmpty()) {
                var in = criteriaBuilder.in(root.get("gender"));
                filter.getGenders()
                        .forEach(
                                c -> {
                                    in.value(c);
                                });
                predicates.add(in);
            }

            if (!filter.getSubsDescriptions().isEmpty()) {
                var in = criteriaBuilder.in(root.get("description"));
                filter.getSubsDescriptions()
                        .forEach(
                                c -> {
                                    in.value(c);
                                });
                predicates.add(in);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    default Specification<MemberSummaryEntity> getMemberSpecificationWithId(
            MemberFilter filter, Integer id) {
        return (root, query, criteriaBuilder) -> {
            var base = getMemberSpecification(filter);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(base.toPredicate(root, query, criteriaBuilder));
            predicates.add(criteriaBuilder.equal(root.get("id"), id));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
