package com.teamof4.mogu.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;



public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    public SecurityContext createSecurityContext(WithMockCustomUser withMockCustomUser) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Authentication auth =
                new UsernamePasswordAuthenticationToken(withMockCustomUser.userId(), null, AuthorityUtils.NO_AUTHORITIES);
        context.setAuthentication(auth);
        return context;
    }
}
