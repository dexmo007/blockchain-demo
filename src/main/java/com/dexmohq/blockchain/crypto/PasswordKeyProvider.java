package com.dexmohq.blockchain.crypto;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

public class PasswordKeyProvider implements KeyProvider {

    private final String password;

    public PasswordKeyProvider(String password) {
        this.password = password;
    }

    @Override
    public Key getKey() {
        final HashCode hashed = Hashing.sha256().hashString(password, StandardCharsets.UTF_8);
        final byte[] key = new byte[16];
        hashed.writeBytesTo(key, 0, 16);
        return new SecretKeySpec(key, "AES");
    }

}
