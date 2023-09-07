package fr.progilone.pgcn.service.util;

import static org.junit.jupiter.api.Assertions.*;

import fr.progilone.pgcn.exception.PgcnTechnicalException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.CharEncoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by Sébastien on 29/12/2016.
 */
public class CryptoServiceTest {

    private CryptoService service;

    @BeforeEach
    public void setUp() {
        service = new CryptoService();
        ReflectionTestUtils.setField(service, "password", "azerty");
    }

    @Test
    public void testEncryptAndDecrypt() throws PgcnTechnicalException {
        final String text = "chuutttt, données secrètes à cacher";

        // Encrypt
        final String encrypted = service.encrypt(text);
        assertNotNull(encrypted);

        // System.out.println(encrypted);

        // Decrypt
        final String decrypted = service.decrypt(encrypted);
        assertEquals(text, decrypted);
    }

    @Disabled
    @Test
    public void testDecrypt() throws PgcnTechnicalException {
        final String encrypted = "xxfS/PpEV2Dqmali/z04lt/vjDaFCm+U0zMd1iQTMWU=";
        ReflectionTestUtils.setField(service, "password", "PhoOpJaiCwy3Jos");
        ReflectionTestUtils.setField(service, "salt", "POEvsixIf+LyzBkDpEsM5ngX8+mLNbGJDLpPlqhgrGw=");

        // Pour générer les encryptés
        System.out.println(service.encrypt("random password to encrypt"));
        final String decrypted = service.decrypt(encrypted);
        System.out.println("decrypted = " + decrypted);
    }

    @Disabled
    @Test
    public void generateSalt() throws NoSuchAlgorithmException {
        // Génération de la clé
        final KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(256);
        final SecretKey key0 = kgen.generateKey();
        System.out.println(Base64.getEncoder().encodeToString(key0.getEncoded()));
    }

    @Disabled
    @Test
    public void testFullProcess() throws NoSuchAlgorithmException,
                                  NoSuchPaddingException,
                                  InvalidKeyException,
                                  IOException,
                                  InvalidAlgorithmParameterException,
                                  InvalidKeySpecException {

        final String test = "Toto fait du vélo";
        System.out.println("Texte initial: " + test);

        final char[] password = "azerty".toCharArray();
        final byte[] salt = "bonne année 2017".getBytes(CharEncoding.UTF_8);

        final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final KeySpec spec = new PBEKeySpec(password, salt, 32768, 128);    // password, salt, iterationCount, keyLength
        SecretKey key = factory.generateSecret(spec);
        key = new SecretKeySpec(key.getEncoded(), "AES");

        System.out.println("key base64 (" + key.getAlgorithm()
                           + ","
                           + key.getFormat()
                           + "): "
                           + new String(Base64.getEncoder().encode(key.getEncoded())));

        // Encrypt cipher
        final Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final IvParameterSpec ivParameterSpec0 = new IvParameterSpec(key.getEncoded());
        encryptCipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec0);

        // Encrypt
        final byte[] encryptedBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, encryptCipher)) {
                cipherOutputStream.write(test.getBytes(CharEncoding.UTF_8));
            }
            encryptedBytes = outputStream.toByteArray();
        }
        System.out.println("Encrypted = " + new String(encryptedBytes));

        // Decrypt cipher
        final Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(key.getEncoded());
        decryptCipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

        // Decrypt
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); ByteArrayInputStream inStream = new ByteArrayInputStream(encryptedBytes)) {
            try (CipherInputStream cipherInputStream = new CipherInputStream(inStream, decryptCipher)) {
                final byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = cipherInputStream.read(buf)) >= 0) {
                    outputStream.write(buf, 0, bytesRead);
                }
            }
            System.out.println("Decrypted = " + new String(outputStream.toByteArray()));
        }
    }
}
