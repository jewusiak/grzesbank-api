package pl.jewusiak.grzesbankapi.utils;

import jakarta.persistence.AttributeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * Converter encrypts/decrypts using AES-256 with GCM
 */
@Service
public class AesStringAttributeEncryptor implements AttributeConverter<String, String> {

    public AesStringAttributeEncryptor() throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
    }

    private Key key;

    private final Cipher cipher;


    @Value("${pl.jewusiak.grzesbankapi.crypto.key}") private String keyB64;

    private Key getKey() {
        if (key == null) {
            var decodedKey = Base64.getDecoder().decode(keyB64);
            key = new SecretKeySpec(decodedKey, "AES");
        }
        return key;
    }


    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            var rand = new SecureRandom();

            var iv = new byte[16];
            rand.nextBytes(iv);
            GCMParameterSpec ivParameterSpec = new GCMParameterSpec(iv.length * 8, iv);

            cipher.init(Cipher.ENCRYPT_MODE, getKey(), ivParameterSpec, rand);

            byte[] cipherText = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            var result = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(result);
        } catch (BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException |
                 IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(dbData);

            byte[] iv = new byte[16];
            byte[] cipherText = new byte[encryptedData.length - 16];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            System.arraycopy(encryptedData, iv.length, cipherText, 0, cipherText.length);

            GCMParameterSpec ivspec = new GCMParameterSpec(iv.length * 8, iv);

            cipher.init(Cipher.DECRYPT_MODE, getKey(), ivspec);

            byte[] decryptedText = cipher.doFinal(cipherText);

            return new String(decryptedText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            //e.printStackTrace(); //todo: implement proper exception handling
            throw new RuntimeException(e);
        }
    }
}
