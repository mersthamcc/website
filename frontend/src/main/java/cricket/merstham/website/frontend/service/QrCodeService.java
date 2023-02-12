package cricket.merstham.website.frontend.service;

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import cricket.merstham.website.frontend.configuration.ClubConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;

import static com.google.zxing.BarcodeFormat.QR_CODE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.text.MessageFormat.format;

@Service
public class QrCodeService {

    private final ClubConfiguration configuration;

    @Autowired
    public QrCodeService(ClubConfiguration configuration) {
        this.configuration = configuration;
    }

    public BufferedImage getQrCode(String data) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix =
                    writer
                            .encode(
                                    data,
                                    QR_CODE,
                                    300,
                                    300);

            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public String asDataUrl(BufferedImage qrCode) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(qrCode, "png", baos);
            return "data:image/png;base64," +
                    Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTotpSecretQrCode(String secret, String email) {
        var qrData = format(
                "otpauth://totp/{0}:{1}?secret={2}&issuer={0}",
                URLEncoder.encode(configuration.getClubName(), UTF_8),
                email,
                secret);

        return asDataUrl(getQrCode(qrData));
    }
}
