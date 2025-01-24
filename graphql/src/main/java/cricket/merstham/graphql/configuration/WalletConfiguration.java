package cricket.merstham.graphql.configuration;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.walletobjects.Walletobjects;
import com.google.api.services.walletobjects.model.CardRowTemplateInfo;
import com.google.api.services.walletobjects.model.CardRowTwoItems;
import com.google.api.services.walletobjects.model.CardTemplateOverride;
import com.google.api.services.walletobjects.model.ClassTemplateInfo;
import com.google.api.services.walletobjects.model.FieldReference;
import com.google.api.services.walletobjects.model.FieldSelector;
import com.google.api.services.walletobjects.model.GenericClass;
import com.google.api.services.walletobjects.model.TemplateItem;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

import static java.text.MessageFormat.format;

@Configuration
public class WalletConfiguration {

    public static final String CLASS_NAME_PATTERN = "{0}.{1}";
    public static final String CATEGORY = "CATEGORY";
    public static final String YEAR = "YEAR";

    @Bean
    @Singleton
    @Named("appleSigningCertificate")
    public X509Certificate getAppleSigningCertificate(
            @Value("${configuration.wallet.apple.signing-certificate}") String certificateFilename)
            throws CertificateException, FileNotFoundException {
        return loadCertificateFromFile(certificateFilename);
    }

    @Bean
    @Singleton
    @Named("appleSigningKey")
    public PrivateKey getAppleSigningKey(
            @Value("${configuration.wallet.apple.signing-key}") String keyFilename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return loadPrivateKeyFromFile(keyFilename);
    }

    @Bean
    @Singleton
    @Named("appleIntermediaryCertificate")
    public X509Certificate getAppleIntermediaryCertificate(
            @Value("${configuration.wallet.apple.apple-intermediary-ca-cert}")
                    String certificateFilename)
            throws CertificateException, FileNotFoundException {
        return loadCertificateFromFile(certificateFilename);
    }

    private X509Certificate loadCertificateFromFile(String certificateFilename)
            throws CertificateException, FileNotFoundException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream(certificateFilename);
        return (X509Certificate) fact.generateCertificate(is);
    }

    @Bean
    public Walletobjects getWalletObjects(
            GoogleCredentials credentials,
            @Value("${configuration.wallet.google.application-name}") String googleApplicationName)
            throws GeneralSecurityException, IOException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Walletobjects.Builder(
                        httpTransport,
                        GsonFactory.getDefaultInstance(),
                        new HttpCredentialsAdapter(credentials))
                .setApplicationName(googleApplicationName)
                .build();
    }

    @Bean
    @Named("googleWalletClass")
    public String googleWalletClass(
            Walletobjects walletobjects,
            @Value("${configuration.wallet.google.issuer}") String issuerId,
            @Value("${configuration.wallet.google.class-name}") String className)
            throws IOException {

        var qualifiedClassName = format(CLASS_NAME_PATTERN, issuerId, className);
        GenericClass classDefinition = createGoogleWalletClass(qualifiedClassName);

        try {
            walletobjects.genericclass().get(qualifiedClassName).execute();

            walletobjects.genericclass().update(qualifiedClassName, classDefinition).execute();
            return String.format("%s.%s", issuerId, className);
        } catch (GoogleJsonResponseException ex) {
            if (ex.getStatusCode() != 404) {
                return qualifiedClassName;
            }
        }

        GenericClass response = walletobjects.genericclass().insert(classDefinition).execute();
        return response.getId();
    }

    private GenericClass createGoogleWalletClass(String qualifiedClassName) {
        return new GenericClass()
                .setId(qualifiedClassName)
                .setMultipleDevicesAndHoldersAllowedStatus("MULTIPLE_HOLDERS")
                .setClassTemplateInfo(
                        new ClassTemplateInfo()
                                .setCardTemplateOverride(
                                        new CardTemplateOverride()
                                                .setCardRowTemplateInfos(
                                                        List.of(
                                                                new CardRowTemplateInfo()
                                                                        .setTwoItems(
                                                                                new CardRowTwoItems()
                                                                                        .setStartItem(
                                                                                                new TemplateItem()
                                                                                                        .setFirstValue(
                                                                                                                new FieldSelector()
                                                                                                                        .setFields(
                                                                                                                                List
                                                                                                                                        .of(
                                                                                                                                                new FieldReference()
                                                                                                                                                        .setFieldPath(
                                                                                                                                                                "object.textModulesData['CATEGORY']")))))
                                                                                        .setEndItem(
                                                                                                new TemplateItem()
                                                                                                        .setFirstValue(
                                                                                                                new FieldSelector()
                                                                                                                        .setFields(
                                                                                                                                List
                                                                                                                                        .of(
                                                                                                                                                new FieldReference()
                                                                                                                                                        .setFieldPath(
                                                                                                                                                                "object.textModulesData['YEAR']"))))))))));
    }

    private PrivateKey loadPrivateKeyFromFile(String keyFilename)
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
}
