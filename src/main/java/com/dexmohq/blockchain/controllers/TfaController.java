package com.dexmohq.blockchain.controllers;

import com.dexmohq.blockchain.config.JwtProperties;
import com.dexmohq.blockchain.model.User;
import com.dexmohq.blockchain.model.UserRepository;
import org.jboss.aerogear.security.otp.Totp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping(path = "/2fa")
@PreAuthorize("hasAuthority('PRE_2FA')")
public class TfaController {

    private static final Logger log = LoggerFactory.getLogger(TfaController.class);

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    @Qualifier("tokenServices")
//    private DefaultTokenServices tokenServices;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private DefaultTokenServices tokenServices;

    @PostMapping
    public OAuth2AccessToken verify2fa(@RequestParam("code") String code,
                                       OAuth2Authentication authentication) {
        final String username = (String) authentication.getPrincipal();
        final User actualUser = userRepository.findByUsername(username);
        final Totp totp = new Totp(actualUser.getTwoFactorAuthenticationSecret());
        try {
            if (!totp.verify(code)) {
                throw new BadCredentialsException("");
            }
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("");
        }
        final UsernamePasswordAuthenticationToken fullAuthentication =
                new UsernamePasswordAuthenticationToken(actualUser, null, actualUser.getAuthorities());
        fullAuthentication.setDetails(authentication.getDetails());
        final OAuth2Request storedRequest = authentication.getOAuth2Request();
        final OAuth2Request newRequest = new OAuth2Request(storedRequest.getRequestParameters(), storedRequest.getClientId(),
                storedRequest.getAuthorities(), true, storedRequest.getScope(),
                storedRequest.getResourceIds(), null, null, Collections.emptyMap());
        final OAuth2Authentication oAuth = new OAuth2Authentication(newRequest,
                fullAuthentication);
        return tokenServices.createAccessToken(oAuth);
    }

}
