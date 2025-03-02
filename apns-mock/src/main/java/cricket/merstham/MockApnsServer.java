package cricket.merstham;

import com.eatthepath.pushy.apns.server.MockApnsServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class MockApnsServer {
    private static final Logger LOG = LoggerFactory.getLogger(MockApnsServer.class);

    public static void main(String[] args) throws SSLException, SQLException {
        LOG.info("Starting MockApnsServer");
        var deviceStore = new DeviceStore();
        var badTokens = List.of(System.getenv("MOCK_APNS_SERVER_BAD_TOKENS").split(","));
        var server =
                new MockApnsServerBuilder()
                        .setListener(new PassApnsMockListener(deviceStore))
                        .setHandlerFactory(
                                sslSession ->
                                        new MockPushNotificationHandler(deviceStore, badTokens))
                        .setServerCredentials(
                                new File(System.getenv("MOCK_APNS_SERVER_CERTIFICATE")),
                                new File(System.getenv("MOCK_APNS_SERVER_KEY")),
                                System.getenv("MOCK_APNS_SERVER_KEY_PASSWORD"))
                        .setTrustedClientCertificateChain(
                                new File(System.getenv("MOCK_APNS_SERVER_CERTIFICATE")))
                        .generateApnsUniqueId(true)
                        .build();
        server.start(8888);
    }
}
