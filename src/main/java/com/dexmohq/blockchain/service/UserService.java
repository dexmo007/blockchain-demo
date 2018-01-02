package com.dexmohq.blockchain.service;

import com.dexmohq.blockchain.model.User;
import org.springframework.security.core.GrantedAuthority;

public interface UserService {

    User registerNew(String username, String password);

    User addAuthority(User user, GrantedAuthority authority);

    User enable2fa(User user);

}
