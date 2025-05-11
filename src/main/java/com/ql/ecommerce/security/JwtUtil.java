package com.ql.ecommerce.security;


import com.ql.ecommerce.exception.InvalidTokenException;
import com.ql.ecommerce.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {

    private final Logger logger= LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access_token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh_token.expiration}")
    private Long refreshTokenExpiration;

    public String generateJwtToken(UserDetails userDetails,Long userId,Long expiration) {
        Date expirationTime = new Date(System.currentTimeMillis() + expiration);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId",userId)
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserIdFromToken(String token){
          Claims claims= Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
          return claims.get("userId", Long.class);
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }  catch(ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
            throw new TokenExpiredException("JWT token is expired");
        }catch ( Exception e){
            logger.error("Exception while validating token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid JWT token from validate token");
        }
    }

}
