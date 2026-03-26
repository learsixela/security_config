package com.seguridad.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Proveedor de tokens JWT para autenticación
 * Responsable de generar, validar y extraer información de tokens JWT
 * JJWT 0.12.3 - Sintaxis actualizada sin métodos deprecated
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:mySecretKeyForJwtTokenGenerationAndValidationPurposesOnly123456789}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshTokenExpirationMs;

    /**
     * Genera un token JWT a partir de la autenticación
     * @param authentication Objeto Authentication de Spring Security
     * @return Token JWT
     */
    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        String roles = user.getAuthorities().toString();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Genera un token de refresco (refresh token)
     * @param username Nombre de usuario
     * @return Refresh token
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .subject(username)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extrae el username del token JWT
     * @param token Token JWT
     * @return Username
     */
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Extrae todos los claims del token JWT
     * @param token Token JWT
     * @return Claims
     */
    public Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida si el token JWT es válido
     * @param token Token JWT
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Verifica si el token ha expirado
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Obtiene el tiempo de expiración del token en milisegundos
     * @return Tiempo de expiración configurado
     */
    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
