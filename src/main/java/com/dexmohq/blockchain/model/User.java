package com.dexmohq.blockchain.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "BC_USER")
public class User implements UserDetails {

    private Long userId;
    private String password;
    private String username;

    private Set<GrantedAuthority> authorities;

    private String twoFactorAuthenticationSecret;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "AUTHORITY")
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "USER_AUTHORITES", joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"USER_ID", "AUTHORITY"}))
    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    @Column(name = "PASSWORD", length = 113, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    @Column(name = "USERNAME", unique = true)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "TFA_SECRET")
    public String getTwoFactorAuthenticationSecret() {
        return twoFactorAuthenticationSecret;
    }

    public void setTwoFactorAuthenticationSecret(String twoFactorAuthenticationSecret) {
        this.twoFactorAuthenticationSecret = twoFactorAuthenticationSecret;
    }

    @Transient
    public boolean is2faEnabled() {
        return twoFactorAuthenticationSecret != null;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }
}
