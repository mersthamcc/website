package cricket.merstham.shared.helpers;

import org.apache.tomcat.util.codec.binary.Base64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
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

public class KeyHelper {

    private KeyHelper() {}

    public static PrivateKey loadPrivateKeyFromFile(String keyFilename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String key = new String(Files.readAllBytes(Path.of(keyFilename)), Charset.defaultCharset());

        String stripped =
                key.replace("-----BEGIN PRIVATE KEY-----", "") // pragma: allowlist secret
                        .replaceAll(System.lineSeparator(), "")
                        .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(stripped);

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
}
