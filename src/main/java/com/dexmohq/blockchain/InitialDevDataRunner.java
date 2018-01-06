package com.dexmohq.blockchain;

import com.dexmohq.blockchain.config.JwtProperties;
import com.dexmohq.blockchain.model.Role;
import com.dexmohq.blockchain.model.User;
import com.dexmohq.blockchain.model.UserRepository;
import com.dexmohq.blockchain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;

@Component
public class InitialDevDataRunner implements CommandLineRunner {//todo activate this runner only in dev profile

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception {
        /*
        SAMPLE CLIENTS
         */
        JdbcClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
//        clientDetailsService.setPasswordEncoder(passwordEncoder);
        try {
            clientDetailsService.removeClientDetails("08154711");
        } catch (NoSuchClientException e) {
            // ignore
        }
        clientDetailsService.addClientDetails(
                new BaseClientDetails() {{
                    setClientId("08154711");
                    setClientSecret("supersecret");
                    setAuthorizedGrantTypes(Collections.singletonList("password"));
                    setScope(Arrays.asList("read", "write"));
                    setResourceIds(Collections.singletonList(jwtProperties.getApiResourceId()));
                }}
        );

        /*
        SAMPLE USERS
         */
        final User admin = userService.registerNew("admin", "admin");
        userService.addAuthority(admin, Role.ADMIN);
        admin.setTwoFactorAuthenticationSecret("7QIR7H3J2BPZDZHR");
        userRepository.save(admin);
        userService.registerNew("user", "user");
    }
}
