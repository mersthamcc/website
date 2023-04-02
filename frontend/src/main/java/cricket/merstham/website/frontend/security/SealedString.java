package cricket.merstham.website.frontend.security;

import lombok.SneakyThrows;
import org.springframework.security.crypto.encrypt.Encryptors;

import java.io.Serial;
import java.io.Serializable;

public class SealedString implements Serializable {
    @Serial private static final long serialVersionUID = 3713044937468880046L;
    private final String cipherText;

    @SneakyThrows()
    public SealedString(String plainText, String password, String salt) {
        var encryptor = Encryptors.text(password, salt);
        this.cipherText = encryptor.encrypt(plainText);
    }

    @SneakyThrows()
    public String decrypt(String password, String salt) {
        var encryptor = Encryptors.text(password, salt);
        return encryptor.decrypt(this.cipherText);
    }
}
