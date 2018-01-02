package com.dexmohq.blockchain.config;

import com.dexmohq.blockchain.model.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class TfaAwareAccessDeniedHandler implements AccessDeniedHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }

        final Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.size() == 1 && authorities.contains(Role.PRE_2FA)) {
            redirectStrategy.sendRedirect(request, response, "/2fa");
            return;
        }
        redirectStrategy.sendRedirect(request, response, "/unauthorized");
    }
}
