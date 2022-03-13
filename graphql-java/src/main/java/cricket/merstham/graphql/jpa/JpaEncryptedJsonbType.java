package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static javax.crypto.Cipher.DECRYPT_MODE;

public class JpaEncryptedJsonbType extends JpaJsonbType {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private final SecureRandom random = new SecureRandom();
    private final SecretKey secretKey;

    public JpaEncryptedJsonbType(String secret) {
        try {
            this.secretKey = new SecretKeySpec(Hex.decodeHex(secret), "AES");
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        final String data = rs.getString(names[0]);
        if (isNull(data)) {
            return null;
        }
        try {
            EncryptionEnvelope envelope = mapper.readValue(data.getBytes(UTF_8), EncryptionEnvelope.class);
            return decrypt(envelope);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decrypt and parse JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (isNull(value)) {
            st.setNull(index, Types.OTHER);
            return;
        }
        try {
            byte[] jsonEncoded = mapper.writeValueAsBytes(value);
            EncryptionEnvelope envelope = encrypt(jsonEncoded, generateIv());

            final StringWriter w = new StringWriter();
            mapper.writeValue(w, envelope);
            w.flush();
            st.setObject(index, w.toString(), Types.OTHER);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to encrypt and convert to JSONB: " + ex.getMessage(), ex);
        }
    }

    private byte[] generateIv() {
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;
    }

    private JsonNode decrypt(EncryptionEnvelope envelope) throws DecoderException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        byte[] decoded = Base64.getDecoder().decode(envelope.getEncrypted());
        byte[] iv = Hex.decodeHex(envelope.getIv());
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(decoded);
        return mapper.readTree(decrypted);
    }

    private EncryptionEnvelope encrypt(final byte[] data, final byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        return EncryptionEnvelope.builder()
                .iv(Hex.encodeHexString(iv))
                .encrypted(Base64.getEncoder().encodeToString(cipher.doFinal(data)))
                .build();
    }
}
