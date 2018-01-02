package com.dexmohq.blockchain.service;

import com.dexmohq.blockchain.model.Role;
import com.dexmohq.blockchain.model.User;
import com.dexmohq.blockchain.model.UserRepository;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerNew(String username, String password) {
        final User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encoder.encode(password));
        newUser.setAuthorities(new HashSet<>(Collections.singleton(Role.USER)));
        return userRepository.save(newUser);
    }

    @Override
    public User addAuthority(User user, GrantedAuthority authority) {
        final HashSet<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());
        authorities.add(authority);
        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

    @Override
    public User enable2fa(User user) {
        final String secret = Base32.random();
        user.setTwoFactorAuthenticationSecret(secret);
        return userRepository.save(user);
    }
}
