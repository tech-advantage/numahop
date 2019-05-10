package fr.progilone.pgcn.service.util;

import fr.progilone.pgcn.exception.PgcnTechnicalException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Service permettant d'encrypter / décrypter des données
 * <p>
 * Created by Sébastien on 30/12/2016.
 */
@Service
public class CryptoService {

    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ENC_ALGORITHM = "AES";
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String KEY_DEFAULT_SALT = "x9xyXNckToF0m2GQhjfGYmQIoRjRAfDUY9K1vwOa5Ds=";
    private static final int KEY_ITERATION_COUNT = 32768;
    private static final int KEY_LENGTH = 128;

    @Value("${crypto.password}")
    private String password;

    @Value("${crypto.salt}")
    private String salt;

    /**
     * Encrypte le texte text à l'aide du password et du salt fournis dans la configuration de l'application
     *
     * @param text
     * @return
     * @throws PgcnTechnicalException
     */
    public String encrypt(final String text) throws PgcnTechnicalException {
        return encrypt(text, password, salt);
    }

    /**
     * Décrypte des données cryptées et encodées en base64 à l'aide du password et du salt fournis dans la configuration de l'application
     *
     * @param text
     * @return
     * @throws PgcnTechnicalException
     */
    public String decrypt(final String text) throws PgcnTechnicalException {
        return decrypt(text, password, salt);
    }

    /**
     * Encrypte le texte text à l'aide du password et du salt fournis
     *
     * @param text
     *         texte à crypter
     * @param password
     *         mot de passe pour le cryptage
     * @param salt
     *         salt pour le cryptage
     * @return données cryptées et encodées en base64
     * @throws PgcnTechnicalException
     */
    private String encrypt(final String text, final String password, final String salt) throws PgcnTechnicalException {
        if (text == null) {
            return null;
        }
        try {
            // Initialisation du cipher
            final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            final SecretKey key = getSecretKey(password, salt);
            final IvParameterSpec ivParameterSpec = newIvParameterSpec(cipher.getBlockSize());
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Ajout du vecteur d'initialisation en tête du résultat (pour permettre le décryptage)
            out.write(ivParameterSpec.getIV());
            // Encrypt
            try (CipherOutputStream cipherOut = new CipherOutputStream(out, cipher)) {
                cipherOut.write(text.getBytes(StandardCharsets.UTF_8));
            }

            // Résultat sous forme de chaîne de caractères Base64
            final byte[] encrypted = out.toByteArray();
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | IOException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Décrypte des données cryptées et encodées en base64
     *
     * @param data
     *         données cryptées et encodées en base64
     * @param password
     *         mot de passe pour le décryptage
     * @param salt
     *         salt pour le décryptage
     * @return
     * @throws PgcnTechnicalException
     */
    private String decrypt(final String data, final String password, final String salt) throws PgcnTechnicalException {
        if (data == null) {
            return null;
        }
        try {
            // Données à déchiffrer
            final byte[] encrypted = Base64.getDecoder().decode(data);
            ByteArrayInputStream in = new ByteArrayInputStream(encrypted);

            // Initialisation du cipher
            final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            final SecretKey key = getSecretKey(password, salt);
            final IvParameterSpec ivParameterSpec = readIv(in, cipher.getBlockSize());
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

            // Decrypt
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (CipherInputStream cipherIn = new CipherInputStream(in, cipher)) {
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = cipherIn.read(buf)) >= 0) {
                    out.write(buf, 0, bytesRead);
                }
            }

            // Résultat
            return new String(out.toByteArray(), StandardCharsets.UTF_8);

        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | IOException | NoSuchPaddingException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Génération d'une clé secrète à partir d'un mot de passe et d'un salt
     *
     * @param password
     *         Mot de passe permettant de générer la clé de cryptage / décryptage
     * @param salt
     *         tableau d'octets encodés en Base64
     * @return
     * @throws PgcnTechnicalException
     */
    private SecretKey getSecretKey(final String password, final String salt) throws PgcnTechnicalException {
        try {
            final char[] _password = password.toCharArray();
            final byte[] _salt = StringUtils.isEmpty(salt) ? Base64.getDecoder().decode(KEY_DEFAULT_SALT) : Base64.getDecoder().decode(salt);

            final SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            final KeySpec spec = new PBEKeySpec(_password, _salt, KEY_ITERATION_COUNT, KEY_LENGTH);    // password, salt, iterationCount, keyLength
            final SecretKey key = factory.generateSecret(spec);
            return new SecretKeySpec(key.getEncoded(), ENC_ALGORITHM);

        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Création d'un vecteur d'initialisation unique
     *
     * @param size
     *         taille du vecteur d'initialisation
     * @return
     */
    private IvParameterSpec newIvParameterSpec(final int size) {
        final byte[] iv = new byte[size];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * @param in
     *         flux à décoder, préfixé par le vecteur d'initialisation
     * @param size
     *         taille du vecteur d'initialisation
     * @return
     * @throws IOException
     */
    private IvParameterSpec readIv(final InputStream in, final int size) throws IOException {
        final byte[] iv = new byte[size];
        int offset = 0;
        while (offset < size) {
            final int read = in.read(iv, offset, size - offset);
            if (read == -1) {
                throw new IOException("Le flux à décrypter est trop court pour contenir un vecteur d'initialisation de " + size + " octets");
            }
            offset += read;
        }
        return new IvParameterSpec(iv);
    }

}
