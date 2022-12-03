package com.teamof4.mogu.security;

import com.teamof4.mogu.dto.JwtErrorResponseDto;
import com.teamof4.mogu.exception.user.WrongTokenSignatureException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
@Component
public class JwtCustomExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.info("-JwtExceptionFilter 동작 중-");
            System.out.println("request = " + request.getRequestURI());
            System.out.println("response = " + response.getStatus());
            filterChain.doFilter(request, response);
        } catch (JwtException exception) {
            log.info("만료토큰에러");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json; charset=UTF-8");

            response.getWriter().write(JwtErrorResponseDto.of(HttpStatus.UNAUTHORIZED, exception.getMessage()).convertToJson());
        }
    }
}