package cricket.merstham;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static java.text.MessageFormat.format;

public class DeviceStore {
    private static final Logger LOG = LoggerFactory.getLogger(DeviceStore.class);
    private final Connection connection;

    public DeviceStore() throws SQLException {
        var url =
                format(
                        "jdbc:postgresql://localhost:5432/{0}?ssl=false",
                        System.getenv("DATABASE_NAME"));
        connection =
                DriverManager.getConnection(
                        url, System.getenv("DATABASE_USER"), System.getenv("DATABASE_PASSWORD"));
    }

    public Optional<String> getDeviceId(String token) {
        try (PreparedStatement ps =
                connection.prepareStatement(
                        "SELECT device_library_identifier FROM passkit_device_registration WHERE push_token=?")) {
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.ofNullable(rs.getString("device_library_identifier"));
            }
        } catch (SQLException e) {
            LOG.error("Error getting device ID", e);
        }
        return Optional.empty();
    }
}
