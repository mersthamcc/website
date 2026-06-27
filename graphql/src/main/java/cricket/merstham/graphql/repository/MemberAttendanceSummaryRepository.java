package cricket.merstham.graphql.repository;

import cricket.merstham.shared.dto.MemberAttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MemberAttendanceSummaryRepository
        extends JpaRepository<MemberAttendanceSummary, Integer>,
                PagingAndSortingRepository<MemberAttendanceSummary, Integer>,
                JpaSpecificationExecutor<MemberAttendanceSummary> {}
