package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static javax.crypto.Cipher.DECRYPT_MODE;

public abstract class JpaEncryptedBaseConverter {
    // AES/GCM/NoPadding
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private final SecureRandom random = new SecureRandom();
    private final SecretKey secretKey;
    protected final ObjectMapper mapper;

    protected JpaEncryptedBaseConverter(ObjectMapper mapper, String secret) {
        this.mapper = mapper;
        try {
            this.secretKey = new SecretKeySpec(Hex.decodeHex(secret), "AES");
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    protected byte[] generateIv() {
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    protected byte[] decrypt(EncryptionEnvelope envelope)
            throws DecoderException, NoSuchPaddingException, NoSuchAlgorithmException,
                    InvalidAlgorithmParameterException, InvalidKeyException,
                    IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] decoded = Base64.getDecoder().decode(envelope.getEncrypted());
        byte[] iv = Hex.decodeHex(envelope.getIv());
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return cipher.doFinal(decoded);
    }

    protected EncryptionEnvelope encrypt(final byte[] data, final byte[] iv)
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

    protected <T> String wrap(T value, Function<T, byte[]> wrapper) {
        if (isNull(value)) {
            return null;
        }
        try {
            EncryptionEnvelope envelope = encrypt(wrapper.apply(value), generateIv());
            return mapper.writeValueAsString(envelope);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to encrypt and convert to JSONB: " + ex.getMessage(), ex);
        }
    }

    protected <T> T unwrap(String dbData, Function<byte[], T> wrapper) {
        if (isNull(dbData)) {
            return null;
        }
        try {
            EncryptionEnvelope envelope = mapper.readValue(dbData, EncryptionEnvelope.class);
            return wrapper.apply(decrypt(envelope));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decrypt content: " + ex.getMessage(), ex);
        }
    }
}
