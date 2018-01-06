package com.dexmohq.blockchain.crypto;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * Encodes passwords using three steps:
 * <ol>
 * <li>SHA-512 Hashing of the raw password</li>
 * <li>The result is encrypted using the BCrypt algorithm</li>
 * <li>AES-256 encryption of BCrypt result using a key from a keystore; keystore and key passwords must be specified in environment</li>
 * </ol>
 * This is recommended by e.g. Dropbox for password storage.<br>
 * For construction, as {@link KeyProvider KeyProvider} must be passed in. This creates the AES-256 encryption key.
 * <p>
 * <i>
 * Note: We recommend using a key store of type JCEKS to retrieve the key. This is done by the
 * {@link JceksKeyProvider JceksKeyProvider}. Since we use 256-bit AES encryption the Java Cryptography
 * Extension (JCE) Unlimited Strength Jurisdiction Policy Files must be enabled. Those can be acquired at
 * the Oracle website and need to be placed inside "$JAVA_HOME/jre/lib/security".
 * Any pre-existing files must be overridden.
 * </i>
 * </p>
 *
 * @author Henrik Drefs
 * @see com.dexmohq.blockchain.crypto.KeyProvider
 * @see com.dexmohq.blockchain.crypto.JceksKeyProvider
 * @see com.dexmohq.blockchain.crypto.PasswordKeyProvider
 * @see BCrypt
 */
public class Aes256BCryptSha512PasswordEncoder implements PasswordEncoder {

    private final Key aesEncryptionKey;

    public Aes256BCryptSha512PasswordEncoder(KeyProvider keyProvider) {
        this.aesEncryptionKey = keyProvider.getKey();
    }

    @Override
    public String encode(CharSequence rawPassword) {
        final byte[] hashBytes = Hashing.sha512().hashString(rawPassword, StandardCharsets.UTF_8).asBytes();
        final String hashBase64 = BaseEncoding.base64().encode(hashBytes);
        final String bCrypted = BCrypt.hashpw(hashBase64, BCrypt.gensalt());
        return encryptAes256(bCrypted);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        final HashCode hashed = Hashing.sha512().hashString(rawPassword, StandardCharsets.UTF_8);
        final String hashedBase64 = BaseEncoding.base64().encode(hashed.asBytes());
        final String decryptedPassword = decrypt(encodedPassword);
        return BCrypt.checkpw(hashedBase64, decryptedPassword);
    }

    private byte[] randomIv(int size) throws NoSuchAlgorithmException {
        final byte[] iv = new byte[size];
        final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.nextBytes(iv);
        return iv;
    }

    private String encryptAes256(CharSequence raw) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final byte[] iv = randomIv(cipher.getBlockSize());
            cipher.init(Cipher.ENCRYPT_MODE, aesEncryptionKey, new IvParameterSpec(iv));
            final byte[] encrypted = cipher.doFinal(raw.toString().getBytes(StandardCharsets.UTF_8));
            final String encryptedBase64 = BaseEncoding.base64().encode(encrypted);
            final String ivBase64 = BaseEncoding.base64().encode(iv);
            return encryptedBase64 + "$" + ivBase64;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new InternalError(e);
        }
    }

    private String decrypt(String encrypted) {
        try {
            final String[] parts = encrypted.split("\\$");
            final String encryptedPassword = parts[0];
            final String ivBase64 = parts[1];
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            final byte[] iv = BaseEncoding.base64().decode(ivBase64);
            cipher.init(Cipher.DECRYPT_MODE, aesEncryptionKey, new IvParameterSpec(iv));
            final byte[] encryptedPasswordBytes = BaseEncoding.base64().decode(encryptedPassword);
            final byte[] rawBytes = cipher.doFinal(encryptedPasswordBytes);
            return new String(rawBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
            throw new InternalError(e);
        }
    }

}
