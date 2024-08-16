package com.spring.boot.webflux.security.app.service.auth;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.expiration-in-minutes}")
    private Long EXPIRATION_IN_MINUTES;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;


    public String generateToken(UserDetails user, Map<String, Object> extraClaims ){

        //ahora vamos a generar nuestro token

        Date issuedAt= new Date(System.currentTimeMillis());
        Date expiration= new Date((EXPIRATION_IN_MINUTES*60*1000)+ issuedAt.getTime());
        String jwt = Jwts.builder()
                .setClaims(extraClaims)
                //este claim es oblicagorio
                .setSubject(user.getUsername())
                //esta propiedad es la de la fecha de emicion del token
                // es decir cuando se creo el token
                .setIssuedAt(issuedAt)
                //esto es cuando el token expera
                .setExpiration(expiration)
                //esto son las propiedades del header
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                //aqui es donde selecionamos el algoritmo y le pasamos nuestra screct key
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();


        return jwt;
    }

    // este es el metodo que generara nuestro clave secreta
    private SecretKey generateKey() {
        //aqui si te fijas lo estamos decodificando de base64, porque el
        //algoritmo de encriptacion lo necesita asi, para convertirulo a binario
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(passwordDecoded);
    }
}
