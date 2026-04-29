package com.claudev.agenda.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Chiave segreta (in produzione mettila in application.properties!)
    //private static final String SECRET_STRING = "questa_e_una_chiave_segreta_molto_lunga_e_complicata_che_serve_per_superare_i_test_di_sicurezza_2026";

    private final SecretKey key;
    private final long jwtExpiration;

    // estrarre l'username (email) dal token
    public String extractUsername (String token) {
        return extractClaime(token, Claims::getSubject);
    }

    // usa il costruttore per assegnare la secret key, altrimenti l'app non si avvia
    // EDIT DEVOPS iniettiamo i valori dall'application properties
    public  JwtUtil(
            @Value("${jwt.secret}") String secretString,
            @Value("${jwt.expiration:36000000}") long jwtExpiration ) {

        if(secretString.length() < 32) {
            throw  new IllegalArgumentException("JWT deve essere almeno di 32 caratteri!");
        }

        // hmacShaKeyFor richiede un array di byte di almeno 256 bit (32 byte)
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));

        this.jwtExpiration = jwtExpiration;
    }




    // Estrae una singola informazione (Claime)
    public <T> T extractClaime (String token, Function<Claims,T> claimeResolver) {
        final Claims claims = extractAllClaims(token);
        return claimeResolver.apply(claims);
    }

    // verificare il token
    private Claims extractAllClaims (String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    //controlla se e' scaduto'
    private boolean isTokenExpired (String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaime(token,Claims::getExpiration);
    }

    // genera un nuovo token`aggiungendo nome ,data di creazione e scadenza firmando con la chiave segreta
    public String generateToken (String username ) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    //controllo del nome
    //es  controllo se il nome sulla lista coincide con quello sul documento
    public boolean validateToken(String token , String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

}
