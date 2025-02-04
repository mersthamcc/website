package cricket.merstham.graphql.services.hooks.pass;

import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.repository.PassKitDeviceRegistrationEntityRepository;
import cricket.merstham.graphql.services.PasskitUpdateService;
import cricket.merstham.graphql.services.hooks.Hook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ApplePassHook implements Hook<OrderEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(ApplePassHook.class);

    private final PasskitUpdateService passkitUpdateService;
    private final PassKitDeviceRegistrationEntityRepository repository;

    @Autowired
    public ApplePassHook(
            PasskitUpdateService passkitUpdateService,
            PassKitDeviceRegistrationEntityRepository repository) {
        this.passkitUpdateService = passkitUpdateService;
        this.repository = repository;
    }

    @Override
    public void onConfirm(OrderEntity data) {
        LOG.info("Begin ApplePassHook onConfirm hook for order {}", data.getId());
        data.getMemberSubscription()
                .forEach(
                        subscription -> {
                            try {
                                var member = subscription.getMember();
                                var registrations =
                                        repository.findAllByMembersContains(Set.of(member));
                                registrations.forEach(
                                        registration -> {
                                            LOG.info(
                                                    "Sending wallet update notification for member {} device {}",
                                                    member.getId(),
                                                    registration.getDeviceLibraryIdentifier());
                                            if (!passkitUpdateService.sendNotification(
                                                    registration)) {
                                                LOG.warn(
                                                        "Removing failed device {} from member {}",
                                                        registration.getDeviceLibraryIdentifier(),
                                                        member.getId());
                                                registration.getMembers().remove(member);
                                            }
                                        });
                                repository.saveAllAndFlush(registrations).stream()
                                        .filter(r -> r.getMembers().isEmpty())
                                        .forEach(repository::delete);
                                repository.flush();
                            } catch (Exception e) {
                                LOG.error(
                                        "Error while Apple passes related to order {}",
                                        data.getId(),
                                        e);
                            }
                        });
        LOG.info("End ApplePassHook onConfirm hook for order {}", data.getId());
    }
}
