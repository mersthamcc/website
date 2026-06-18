package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MemberAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAttendanceEntityRepository
        extends JpaRepository<MemberAttendanceEntity, String> {}
