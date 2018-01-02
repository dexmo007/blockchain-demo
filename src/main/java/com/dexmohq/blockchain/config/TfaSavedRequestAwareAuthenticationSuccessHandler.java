package com.dexmohq.blockchain.config;

import com.dexmohq.blockchain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TfaSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final HttpSessionRequestCache requestCache;

    TfaSavedRequestAwareAuthenticationSuccessHandler() {
        requestCache = new HttpSessionRequestCache();
        setRequestCache(requestCache);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        final User user = (User) authentication.getPrincipal();
        if (user.is2faEnabled()) {
            onPre2faAuthenticationSuccess(request, response, authentication);
            return;
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void onPre2faAuthenticationSuccess(HttpServletRequest request,
                                               HttpServletResponse response,
                                               Authentication authentication) throws ServletException, IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) {
            if (!response.isCommitted()) {
                getRedirectStrategy().sendRedirect(request, response, "/2fa");
            }
            clearAuthenticationAttributes(request);
            return;
        }
        String targetUrlParameter = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl()
                || (targetUrlParameter != null && StringUtils.hasText(request
                .getParameter(targetUrlParameter)))) {
            requestCache.removeRequest(request, response);
            final String targetUrl = determineTargetUrl(request, response);
            if (!response.isCommitted()) {
                getRedirectStrategy().sendRedirect(request, response, "/2fa?target=" + targetUrl);
            }
            clearAuthenticationAttributes(request);
            return;
        }

        clearAuthenticationAttributes(request);

        // Use the DefaultSavedRequest URL
        String targetUrl = savedRequest.getRedirectUrl();
        getRedirectStrategy().sendRedirect(request, response, "/2fa?target=" + targetUrl);
    }
}
