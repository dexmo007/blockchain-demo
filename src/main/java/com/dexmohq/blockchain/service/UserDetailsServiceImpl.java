package com.dexmohq.blockchain.service;

import com.dexmohq.blockchain.model.Role;
import com.dexmohq.blockchain.model.User;
import com.dexmohq.blockchain.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("A user with name '" + s + "' does not exist");
        }
        if (user.is2faEnabled()) {
            user.setAuthorities(Collections.singleton(Role.PRE_2FA));
        }
        return user;
    }
}
