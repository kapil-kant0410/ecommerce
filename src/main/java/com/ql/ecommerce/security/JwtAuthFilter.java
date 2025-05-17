package com.ql.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ql.ecommerce.dto.ApiResponse;
import com.ql.ecommerce.exception.InvalidToken;
import com.ql.ecommerce.exception.MissingToken;
import com.ql.ecommerce.exception.TokenExpired;
import com.ql.ecommerce.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        logger.info("----------------------------------------------------------------------------------------------");
        String path = request.getRequestURI();

        if (path.startsWith("/api/auth/")) {
            logger.info("In JwtAuth filter Does not need token on this route next it move to controller");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            logger.info("In JwtAuth filter need token on this next validating the token");
            String jwt = parseJwt(request);
            if (jwt == null) {
                throw new MissingToken("JWT token is missing");
            }
            if (jwtUtil.validateJwtToken(jwt)) {
                logger.info("In JwtAuth filter after validation of token");
                String username = jwtUtil.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                logger.info("In JwtAuth filter userDetail object loaded from userDetail service");
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                logger.info("In JwtAuth filter after userDetail object loaded setting authentication in security context holder");
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (MissingToken e) {
            logger.info("Missing JWT token");
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing JWT token", e.getMessage(), request.getRequestURI());
        } catch (TokenExpired e) {
            logger.info("JWT token is expired");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired", e.getMessage(), request.getRequestURI());
        } catch (InvalidToken e) {
            logger.info("Invalid JWT token");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token", e.getMessage(), request.getRequestURI());
        } catch (Exception e) {
            logger.info("Unexpected error in JWT filter");
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error", e.getMessage(), request.getRequestURI());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message, String path) throws IOException {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("error", error);
        errorDetails.put("path", path);
        errorDetails.put("timestamp", Instant.now().toString());

        ApiResponse<?> apiResponse = ApiResponse.error(status, errorDetails, message);

        response.setStatus(status);
        response.setContentType("application/json");

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
