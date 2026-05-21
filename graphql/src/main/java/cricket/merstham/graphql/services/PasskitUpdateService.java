package cricket.merstham.graphql.services;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.entity.PasskitDeviceRegistrationEntity;
import cricket.merstham.graphql.repository.PassKitDeviceRegistrationEntityRepository;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class PasskitUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(PasskitUpdateService.class);
    private static final List<String> UNREGISTER_REASONS =
            List.of("BadDeviceToken", "Unregistered");

    private final String applePassIdentifier;
    private final ApnsClient apnsClient;
    private final PassKitDeviceRegistrationEntityRepository repository;

    @Autowired
    public PasskitUpdateService(
            @Value("${configuration.wallet.apple.pass-identifier}") String applePassIdentifier,
            @Named("WalletUpdateApnsClient") ApnsClient apnsClient,
            PassKitDeviceRegistrationEntityRepository repository) {
        this.applePassIdentifier = applePassIdentifier;
        this.apnsClient = apnsClient;
        this.repository = repository;
    }

    public int sendUpdatesForMember(MemberEntity member) {
        try {
            var devicesRegistered = repository.findAllByMembersContains(Set.of(member));
            var devicesToRemove = new ArrayList<PasskitDeviceRegistrationEntity>();
            devicesRegistered.forEach(
                    registration -> {
                        LOG.info(
                                "Sending wallet update notification for member {} device {}",
                                member.getId(),
                                registration.getDeviceLibraryIdentifier());
                        if (!sendNotification(registration)) {
                            LOG.warn(
                                    "Removing failed device {} from member {}",
                                    registration.getDeviceLibraryIdentifier(),
                                    member.getId());
                            devicesToRemove.add(registration);
                        }
                    });
            repository.deleteAll(devicesToRemove);
            repository.flush();
            return devicesRegistered.size() - devicesToRemove.size();
        } catch (Exception e) {
            LOG.error("Error while sending Apple pass updates for member {}", member.getId(), e);
            return -1;
        }
    }

    public boolean sendNotification(PasskitDeviceRegistrationEntity registration) {
        if (!registration.isValid()) return false;
        return sendNotification(
                registration.getDeviceLibraryIdentifier(), registration.getPushToken());
    }

    private boolean sendNotification(String deviceId, String pushToken) {
        var notification = new SimpleApnsPushNotification(pushToken, applePassIdentifier, "{}");

        var response = apnsClient.sendNotification(notification);

        try {
            var pushNotificationResponse = response.get();

            if (pushNotificationResponse.isAccepted()) {
                LOG.info("Push notification accepted by APNs gateway for {}", deviceId);
            } else {
                var rejectionReason = pushNotificationResponse.getRejectionReason();
                if (rejectionReason.isPresent()) {
                    LOG.warn(
                            "Notification rejected by the APNs gateway: {}", rejectionReason.get());

                    if (UNREGISTER_REASONS.contains(rejectionReason.get())) {
                        return false;
                    }
                }
            }
        } catch (ExecutionException e) {
            LOG.error("Failed to send push notification.", e);
        } catch (InterruptedException e) {
            LOG.error("Interrupted sending notification", e);
            Thread.currentThread().interrupt();
        }
        return true;
    }
}
