package com.dexmohq.blockchain.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Objects;

public class JceksKeyProvider implements KeyProvider {

    private static final String DEFAULT_STORE_PASSWORD_KEY = "password.encoding.storepass";
    private static final String DEFAULT_KEY_PASSWORD_KEY = "password.encoding.keypass";
    private String alias = "jceksaes";
    private String keyStoreLocation = "/security/aes-keystore.jck";
    private String storePassword = null;
    private String keyPassword = null;

    private JceksKeyProvider() {
    }

    public static JceksKeyProvider builder() {
        return new JceksKeyProvider();
    }

    public JceksKeyProvider alias(String alias) {
        this.alias = Objects.requireNonNull(alias);
        return this;
    }

    public JceksKeyProvider keyStoreLocation(String keyStoreLocation) {
        this.keyStoreLocation = Objects.requireNonNull(keyStoreLocation);
        return this;
    }

    public JceksKeyProvider storePassword(String storePassword) {
        this.storePassword = Objects.requireNonNull(storePassword);
        return this;
    }

    public JceksKeyProvider keyPassword(String keyPassword) {
        this.keyPassword = Objects.requireNonNull(keyPassword);
        return this;
    }

    public JceksKeyProvider storePasswordByEnvironment(String key) {
        final String storePassword = System.getProperty(key);
        if (storePassword == null) {
            throw new IllegalArgumentException("Environment variable for store password with key '" + key + "' not found");
        }
        this.storePassword = storePassword;
        return this;
    }

    public JceksKeyProvider keyPasswordByEnvironment(String key) {
        final String keyPassword = System.getProperty(key);
        if (keyPassword == null) {
            throw new IllegalArgumentException("Environment variable for key password with key '" + key + "' not found");
        }
        this.keyPassword = keyPassword;
        return this;
    }

    @Override
    public Key getKey() {
        if (storePassword == null) {
            storePasswordByEnvironment(DEFAULT_STORE_PASSWORD_KEY);
        }
        if (keyPassword == null) {
            keyPasswordByEnvironment(DEFAULT_KEY_PASSWORD_KEY);
        }
        try {
            final KeyStore keyStore = KeyStore.getInstance("JCEKS");
            final FileInputStream inputStream = new FileInputStream(new File(getClass().getResource(keyStoreLocation).toURI()));
            keyStore.load(inputStream, storePassword.toCharArray());
            if (!keyStore.containsAlias(alias)) {
                throw new KeyStoreException("Key store alias '" + alias + "' not found");
            }
            return keyStore.getKey(alias, keyPassword.toCharArray());
        } catch (IOException | CertificateException | URISyntaxException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            throw new IllegalStateException(e);
        }
    }
}
