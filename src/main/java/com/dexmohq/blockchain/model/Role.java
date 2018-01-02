package com.dexmohq.blockchain.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER, ADMIN, PRE_2FA;

    @Override
    public String getAuthority() {
        return name();
    }
}
