package br.com.vfs.marketplacebackend.config.jwt;

import br.com.vfs.marketplacebackend.dto.TokenJwt;
import br.com.vfs.marketplacebackend.dto.UserPrinciple;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);

    private final String jwtSecret;

    private final int jwtExpiration;

    public JwtProvider(@Value("${marketplace.app.jwtSecret}") String jwtSecret,
                       @Value("${marketplace.app.jwtExpiration}") int jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
        this.jwtSecret = jwtSecret;
    }

    public String generateJwtToken(Authentication authentication) {
        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .addClaims(Collections.singletonMap("role", authentication.getAuthorities()))
                .addClaims(Collections.singletonMap("name", userPrincipal.getName()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration*1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature -> Message: {} ", e);
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            LOGGER.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            LOGGER.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty -> Message: {}", e);
        }
        return false;
    }

    public TokenJwt getUserNameFromJwtToken(String token) {
        final Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return TokenJwt.builder()
                .name(claims.get("name").toString())
                .userName(claims.getSubject())
                .authorities(
                        Arrays.stream(claims.get("role").toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()))
                .build();
    }
}
