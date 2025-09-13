package cricket.merstham.shared.helpers;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyHelper {

    private KeyHelper() {}

    public static PrivateKey loadPrivateKeyFromFile(String keyFilename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String key = Files.readString(Path.of(keyFilename), Charset.defaultCharset());

        return loadPrivateKeyFromString(key);
    }

    public static PrivateKey loadPrivateKeyFromString(String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String stripped =
                key.replace("-----BEGIN PRIVATE KEY-----", "") // pragma: allowlist secret
                        .replaceAll(System.lineSeparator(), "")
                        .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(stripped);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

    public static X509Certificate loadCertificateFromFile(String certificateFilename)
            throws CertificateException, FileNotFoundException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream(certificateFilename);
        return (X509Certificate) fact.generateCertificate(is);
    }

    public static X509Certificate loadCertificateFromString(String content)
            throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        return (X509Certificate) fact.generateCertificate(stream);
    }
}
