package cricket.merstham;

import com.eatthepath.pushy.apns.DeliveryPriority;
import com.eatthepath.pushy.apns.server.PushNotificationHandler;
import com.eatthepath.pushy.apns.server.RejectedNotificationException;
import com.eatthepath.pushy.apns.server.RejectionReason;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MockPushNotificationHandler implements PushNotificationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MockPushNotificationHandler.class);
    private static final String APNS_PATH_PREFIX = "/3/device/";
    private static final AsciiString APNS_TOPIC_HEADER = new AsciiString("apns-topic");
    private static final AsciiString APNS_PRIORITY_HEADER = new AsciiString("apns-priority");

    private final DeviceStore deviceStore;
    private final List<String> badTokens;

    public MockPushNotificationHandler(DeviceStore deviceStore, List<String> badTokens) {
        this.deviceStore = deviceStore;
        this.badTokens = badTokens;
    }

    @Override
    public void handlePushNotification(Http2Headers headers, ByteBuf payload)
            throws RejectedNotificationException {
        CharSequence topic = headers.get(APNS_TOPIC_HEADER);
        if (isNull(topic)) {
            throw new RejectedNotificationException(RejectionReason.MISSING_TOPIC);
        }

        Integer priority = headers.getInt(APNS_PRIORITY_HEADER);
        if (nonNull(priority)) {
            try {
                DeliveryPriority.getFromCode(priority);
            } catch (IllegalArgumentException e) {
                throw new RejectedNotificationException(RejectionReason.BAD_PRIORITY);
            }
        }

        LOG.info("Received push notification with topic {}, priority {}", topic, priority);

        var path = headers.get(Http2Headers.PseudoHeaderName.PATH.value());
        if (isNull(path)) {
            throw new RejectedNotificationException(RejectionReason.BAD_PATH);
        }
        String pathString = path.toString();
        if (path.toString().equals(APNS_PATH_PREFIX)) {
            throw new RejectedNotificationException(RejectionReason.MISSING_DEVICE_TOKEN);
        } else if (pathString.startsWith(APNS_PATH_PREFIX)) {
            String deviceToken = pathString.substring(APNS_PATH_PREFIX.length());

            var deviceId = deviceStore.getDeviceId(deviceToken);
            if (deviceId.isEmpty() || badTokens.contains(deviceToken)) {
                throw new RejectedNotificationException(RejectionReason.BAD_DEVICE_TOKEN);
            }
            LOG.info("Matched device {} for push notification", deviceId.get());
        } else {
            throw new RejectedNotificationException(RejectionReason.BAD_PATH);
        }
    }
}
