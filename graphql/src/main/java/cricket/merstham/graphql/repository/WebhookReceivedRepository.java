package cricket.merstham.graphql.repository;

import cricket.merstham.graphql.entity.WebhookReceivedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface WebhookReceivedRepository
        extends JpaRepository<WebhookReceivedEntity, String>,
                PagingAndSortingRepository<WebhookReceivedEntity, String> {
    List<WebhookReceivedEntity> findAllByProcessedFalseAndTypeIn(Collection<String> types);
}
