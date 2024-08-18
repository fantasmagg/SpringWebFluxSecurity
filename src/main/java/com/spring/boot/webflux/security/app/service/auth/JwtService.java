package com.spring.boot.webflux.security.app.service.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private SecretKey key;
    private JwtParser parser;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(generateKey());
        this.parser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    private byte[] generateKey() {
        return Decoders.BASE64.decode(SECRET_KEY);
    }

    public String generate (String username, Map<String, Object> extraClaims){
        JwtBuilder builder = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(key);
        return  builder.compact();
    }

    public String getUserName(String token){
        Claims claims = parser.parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public boolean validate(UserDetails user, String token){
        Claims claims = parser.parseClaimsJws(token).getBody();
        boolean unexpired = claims.getExpiration().after(Date.from(Instant.now()));

        return unexpired && user.getUsername() == claims.getSubject();
    }

}

