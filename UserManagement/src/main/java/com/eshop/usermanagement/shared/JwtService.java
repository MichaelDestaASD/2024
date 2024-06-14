package com.eshop.usermanagement.shared;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private static final String SECRET = "F6D6722549E7E1C1222C45DF0F1E81AD79F099CD6403475F9836DE1999E187E64B84259CCD76B36CB174E4C37B5C03AE6E9C9ADD90CFF71F3D559CF3F9306333";
    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);


    public String generateToken(UserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        claims.put("iss", "https://secure.genuinecoder.com");
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(VALIDITY)))
                .signWith(generateKey())
                .compact();
    }
    private SecretKey generateKey() {
        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(decodedKey);
    }
    public String extractUsername(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getSubject();
    }
    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
    public boolean isTokenValid(String jwt) {
        Claims claims = getClaims(jwt);
        return claims.getExpiration().after(Date.from(Instant.now()));
    }
}
