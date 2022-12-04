package com.teamof4.mogu.security;

import com.sun.istack.NotNull;
import com.teamof4.mogu.constants.JwtConstants;
import com.teamof4.mogu.exception.user.ExpiredTokenException;
import com.teamof4.mogu.exception.user.WrongTokenSignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.teamof4.mogu.constants.JwtConstants.EXPIRED_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException, JwtException {
        try {
            //요청에서 토큰 가져오기
            String token = parsBearerToken(request);
            log.info("-JwtAuthenticationFilter 동작 중-");

            // 토큰 검사하기 및 시큐리티 등록
            if (token != null && !token.equalsIgnoreCase("null")) {
                Long userId = Long.parseLong(tokenProvider.validateAndGetUserId(token));
                log.info("인증된 userId : " + userId);

                //인증 완료 : SecurityContextHolder 에 등록해야 인증된 사용자로 판단
                AbstractAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
        } catch (ExpiredJwtException exception) {
            log.warn("토큰 기한 만료");
            throw new JwtException("만료된 토큰입니다.");
        } catch (SignatureException exception) {
            log.warn("토큰 서명 불일치");
            request.setAttribute("exception", "만료토큰 777");
            response.getWriter().write("만료토큰 123");
            throw new JwtException("서명이 일치하지 않는 토큰입니다.");
        }
        catch (Exception exception) {
            logger.error("Could not set user authentication in security context", exception);
            log.info("토큰 에러");
        }
        filterChain.doFilter(request, response);
    }

    private String parsBearerToken(HttpServletRequest httpServletRequest) {
        String token = Arrays
                .stream(httpServletRequest.getCookies())
                .filter(cookie -> cookie.getName().equals("access-token"))
                .collect(Collectors.toList())
                .get(0).getValue();
        return token;
    }
}
