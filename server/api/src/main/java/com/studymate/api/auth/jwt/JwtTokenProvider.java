package com.studymate.api.auth.jwt;

import io.jsonwebtoken.*;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${secret-key}")
    private String secretKey;
    private Key key;
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public String createToken(String identifier) {

        long validityInMilliseconds = 1000 * 60 * 60;
        Date expiryDate = Date.from(Instant.now().plusMillis(validityInMilliseconds));
        log.info("token created: {}", identifier);
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuer("study mate auth server")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    private String getUserIdentifier(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication getAuthentication(String token) {
        String userIdentifier = getUserIdentifier(token);
        log.info("token parsed: {}", userIdentifier);
        return new UsernamePasswordAuthenticationToken(userIdentifier, null, AuthorityUtils.NO_AUTHORITIES);
    }

    public Optional<String> resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    public boolean validateToken(String token) throws ExpiredJwtException {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (IllegalArgumentException e) {
            log.error("JWT Token is null or empty");
        } catch (MalformedJwtException e) {
            log.error("JWT is invalid");
        } catch (UnsupportedJwtException e) {
            log.error("JWT is unsupported");
        } catch (SignatureException e) {
            log.error("JWT signature is invalid");
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired");
        }
        return false;
    }
}