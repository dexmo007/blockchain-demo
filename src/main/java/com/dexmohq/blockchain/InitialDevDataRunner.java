package com.dexmohq.blockchain;

import com.dexmohq.blockchain.model.Role;
import com.dexmohq.blockchain.model.User;
import com.dexmohq.blockchain.model.UserRepository;
import com.dexmohq.blockchain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialDevDataRunner implements CommandLineRunner {//todo activate this runner only in dev profile

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... strings) throws Exception {
        final User admin = userService.registerNew("admin", "admin");
        userService.addAuthority(admin, Role.ADMIN);
        admin.setTwoFactorAuthenticationSecret("7QIR7H3J2BPZDZHR");
        userRepository.save(admin);
        userService.registerNew("user", "user");
    }
}
