package cricket.merstham;

import com.eatthepath.pushy.apns.server.MockApnsServerListener;
import com.eatthepath.pushy.apns.server.RejectionReason;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.time.Instant;

import static java.text.MessageFormat.format;

public class PassApnsMockListener implements MockApnsServerListener {

    private static final Logger LOG = LoggerFactory.getLogger(PassApnsMockListener.class);

    private final DeviceStore deviceStore;

    public PassApnsMockListener(DeviceStore deviceStore) {
        this.deviceStore = deviceStore;
    }

    @Override
    public void handlePushNotificationAccepted(Http2Headers headers, ByteBuf payload) {
        LOG.info("Accepted push notification");
        LOG.info(
                "{} {} - {}",
                headers.method(),
                headers.path(),
                payload.toString(Charset.defaultCharset()));
        var token = headers.path().toString().split("/")[3];
        var deviceId = deviceStore.getDeviceId(token);
        deviceId.ifPresent(id -> callPassUpdate(id, headers.get("apns-topic").toString()));
    }

    @Override
    public void handlePushNotificationRejected(
            Http2Headers headers,
            ByteBuf payload,
            RejectionReason rejectionReason,
            Instant deviceTokenExpirationTimestamp) {
        LOG.info("Rejected push notification");
        LOG.info("{} {} {}", headers.method(), headers.path(), rejectionReason.name());
        LOG.info("Body = {}", payload.toString(Charset.defaultCharset()));
    }

    private static void callPassUpdate(String deviceId, String topic) {
        LOG.info("Calling API to get updated passes...");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request =
                HttpRequest.newBuilder()
                        .GET()
                        .uri(
                                URI.create(
                                        format(
                                                "http://localhost:8090/passkit/v1/devices/{0}/registrations/{1}",
                                                deviceId, topic)))
                        .build();
        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOG.error("Unexpected response code: {}", response.statusCode());
            }
            LOG.info(response.body());
        } catch (IOException e) {
            LOG.error("Error requesting new passes", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
