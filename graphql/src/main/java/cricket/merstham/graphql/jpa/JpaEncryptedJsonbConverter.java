package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static java.util.Objects.isNull;
import static javax.crypto.Cipher.DECRYPT_MODE;

public class JpaEncryptedJsonbConverter implements AttributeConverter<JsonNode, String> {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private final SecureRandom random = new SecureRandom();
    private final SecretKey secretKey;
    private final ObjectMapper mapper;

    public JpaEncryptedJsonbConverter(
            ObjectMapper objectMapper, @Value("${configuration.database-secret}") String secret) {
        this.mapper = objectMapper;
        try {
            this.secretKey = new SecretKeySpec(Hex.decodeHex(secret), "AES");
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] generateIv() {
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    private JsonNode decrypt(EncryptionEnvelope envelope)
            throws DecoderException, NoSuchPaddingException, NoSuchAlgorithmException,
                    InvalidAlgorithmParameterException, InvalidKeyException,
                    IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] decoded = Base64.getDecoder().decode(envelope.getEncrypted());
        byte[] iv = Hex.decodeHex(envelope.getIv());
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(decoded);
        return mapper.readTree(decrypted);
    }

    private EncryptionEnvelope encrypt(final byte[] data, final byte[] iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                    IllegalBlockSizeException, BadPaddingException,
                    InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        return EncryptionEnvelope.builder()
                .iv(Hex.encodeHexString(iv))
                .encrypted(Base64.getEncoder().encodeToString(cipher.doFinal(data)))
                .build();
    }

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        if (isNull(attribute)) {
            return null;
        }
        try {
            byte[] jsonEncoded = mapper.writeValueAsBytes(attribute);
            EncryptionEnvelope envelope = encrypt(jsonEncoded, generateIv());

            final StringWriter w = new StringWriter();
            mapper.writeValue(w, envelope);
            w.flush();
            return w.toString();
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to encrypt and convert to JSONB: " + ex.getMessage(), ex);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        if (isNull(dbData)) {
            return null;
        }
        try {
            EncryptionEnvelope envelope = mapper.readValue(dbData, EncryptionEnvelope.class);
            return decrypt(envelope);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decrypt and parse JSON: " + ex.getMessage(), ex);
        }
    }
}
