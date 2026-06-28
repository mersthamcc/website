package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MemberAttendanceEntityRepository
        extends JpaRepository<MemberAttendanceEntity, String>,
                PagingAndSortingRepository<MemberAttendanceEntity, String>,
                JpaSpecificationExecutor<MemberAttendanceEntity> {}
