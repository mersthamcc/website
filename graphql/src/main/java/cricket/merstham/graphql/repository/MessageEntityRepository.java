package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageEntityRepository extends JpaRepository<MessageEntity, String> {}
