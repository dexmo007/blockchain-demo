package com.dexmohq.blockchain.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "blockchain")
@EnableConfigurationProperties
public class JwtProperties {

    private String apiResourceId;
    private String signingKey;
    private int encodingStrength;
    private String securityRealm;

    public String getApiResourceId() {
        return apiResourceId;
    }

    public void setApiResourceId(String apiResourceId) {
        this.apiResourceId = apiResourceId;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public void setSigningKey(String signingKey) {
        this.signingKey = signingKey;
    }

    public int getEncodingStrength() {
        return encodingStrength;
    }

    public void setEncodingStrength(int encodingStrength) {
        this.encodingStrength = encodingStrength;
    }

    public String getSecurityRealm() {
        return securityRealm;
    }

    public void setSecurityRealm(String securityRealm) {
        this.securityRealm = securityRealm;
    }
}
