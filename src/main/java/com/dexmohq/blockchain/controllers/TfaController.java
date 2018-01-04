package com.dexmohq.blockchain.controllers;

import com.dexmohq.blockchain.model.User;
import com.dexmohq.blockchain.model.UserRepository;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@Controller
//@RequestMapping(path = "/2fa")
public class TfaController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String view2fa(@RequestParam(name = "target", required = false) String targetUrl, Model model) {
        model.addAttribute("target", targetUrl);
        return "2fa";
    }

    @PostMapping
    public String verify2fa(@RequestParam("code") String code,
                            @RequestParam(name = "target", required = false) String target) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final User user = (User) authentication.getPrincipal();

        final Totp totp = new Totp(user.getTwoFactorAuthenticationSecret());
        try {
            if (!totp.verify(code)) {
                return errorResponse(target);
            }
        } catch (NumberFormatException e) {
            return errorResponse(target);
        }

        final User actualUser = userRepository.findOne(user.getUserId());
        final UsernamePasswordAuthenticationToken fullAuthentication =
                new UsernamePasswordAuthenticationToken(actualUser, null, actualUser.getAuthorities());
        fullAuthentication.setDetails(authentication.getDetails());
        SecurityContextHolder.getContext().setAuthentication(fullAuthentication);
        return "redirect:" + (target == null ? "/" : target);
    }

    private String errorResponse(String target) {
        if (target == null) {
            return "redirect:/2fa?error";
        }
        return "redirect:/2fa?target=" + target + "&error";
    }

}
