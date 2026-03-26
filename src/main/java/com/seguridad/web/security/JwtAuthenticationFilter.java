package com.seguridad.web.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filtro JWT para autenticación basada en tokens
 * Se ejecuta una vez por solicitud (OncePerRequestFilter)
 * Extrae el token JWT del header Authorization y lo valida
 * JJWT 0.12.3 - Sintaxis actualizada
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Value("${app.jwt.secret:mySecretKeyForJwtTokenGenerationAndValidationPurposesOnly123456789}")
    @SuppressWarnings("unused")
    private String jwtSecret;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                String username = tokenProvider.getUsernameFromToken(token);
                List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromToken(token);

                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("No se pudo establecer la autenticación de usuario: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization
     * Espera el formato: "Bearer <token>"
     * @param request Solicitud HTTP
     * @return Token JWT o null si no está presente
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Extrae las autoridades (roles) del token JWT del claim "roles"
     * Parsea el formato: "[ROLE_USER, ROLE_ADMIN]" o similar
     * @param token Token JWT
     * @return Lista de autoridades/roles
     */
    private List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        try {
            Claims claims = tokenProvider.getClaimsFromToken(token);
            String rolesString = (String) claims.get("roles");

            if (StringUtils.hasText(rolesString)) {
                // Parsear roles del formato [ROLE_USER, ROLE_ADMIN]
                Pattern pattern = Pattern.compile("ROLE_\\w+");
                Matcher matcher = pattern.matcher(rolesString);

                while (matcher.find()) {
                    String role = matcher.group();
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }
        } catch (Exception e) {
            logger.error("Error extrayendo autoridades del token: {}", e);
        }
        return authorities;
    }
}
