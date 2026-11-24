
package com.Meditrack.Meditrack.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtServicio {

    private final SecretKey claveSecreta;
    private final long expiracionMs;

    public JwtServicio(
            @Value("${meditrack.jwt.secret}") String secreto,
            @Value("${meditrack.jwt.expiracion-ms}") long expiracionMs) {

        this.claveSecreta = Keys.hmacShaKeyFor(
                secreto.getBytes(StandardCharsets.UTF_8));

        this.expiracionMs = expiracionMs;
    }

    public String generarToken(
            Long idUsuario, String correo, String rol) {

        Date fechaActual = new Date();

        Date fechaExpiracion =
                new Date(fechaActual.getTime() + expiracionMs);

        return Jwts.builder()
                .subject(String.valueOf(idUsuario))
                .claim("correo", correo)
                .claim("rol", rol)
                .issuedAt(fechaActual)
                .expiration(fechaExpiracion)
                .signWith(claveSecreta)
                .compact();
    }

    public Claims validarToken(String token) {

        return Jwts.parser()
                .verifyWith(claveSecreta)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
