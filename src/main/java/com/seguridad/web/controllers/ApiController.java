package com.seguridad.web.controllers;

import com.seguridad.web.Dto.UserDto;
import com.seguridad.web.models.Usuario;
import com.seguridad.web.security.JwtTokenProvider;
import com.seguridad.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador API REST para autenticación y operaciones aseguradas con JWT
 * Maneja login, registro, refresh de tokens y endpoints protegidos
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Login - Autentica usuario y retorna JWT token
     * @param userDto Credenciales del usuario (username, password)
     * @return Token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        // Validar errores
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getUsername(),
                            userDto.getPassword()
                    )
            );

            // Establecer autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar JWT token
            String token = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDto.getUsername());

            // Obtener usuario para retornar datos
            Usuario usuario = userService.getUserByUsername(userDto.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login exitoso");
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            response.put("user", new UserDto(
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getRole()
            ));

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Credenciales inválidas");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Registro - Registra un nuevo usuario en el sistema
     * @param userDto Datos del nuevo usuario
     * @return Usuario registrado y JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        // Validar errores
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // Guardar nuevo usuario
            userService.saveUser(userDto);

            // Autenticar automáticamente al nuevo usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getUsername(),
                            userDto.getPassword()
                    )
            );

            // Generar tokens
            String token = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDto.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado exitosamente");
            response.put("token", token);
            response.put("refreshToken", refreshToken);
            response.put("user", userDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al registrar usuario");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Refresh Token - Genera un nuevo JWT token usando el refresh token
     * @param refreshTokenRequest Objeto con el refresh token
     * @return Nuevo JWT token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.get("refreshToken");

            if (!jwtTokenProvider.validateToken(refreshToken)) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Refresh token inválido o expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

            // Generar nuevo token
            Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    username, null, null
            );
            String newToken = jwtTokenProvider.generateToken(authentication);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Token renovado exitosamente");
            response.put("token", newToken);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al renovar token");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Endpoint protegido - Obtiene información del usuario autenticado
     * Requiere: Token JWT válido
     * @return Datos del usuario autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = userService.getUserByUsername(username);

            UserDto userDto = new UserDto(
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getRole()
            );

            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener datos del usuario");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint protegido - Lista todos los usuarios (solo para ADMIN)
     * Requiere: Token JWT válido + ROLE_ADMIN
     * @return Lista de todos los usuarios
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDto> usuarios = userService.findAllUsers();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener lista de usuarios");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Test endpoint - Verifica que la API esté funcionando
     * @return Mensaje de test
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "API REST funcionando correctamente");
        response.put("timestamp", new java.util.Date().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint protegido - Obtiene información detallada del usuario (para ADMIN)
     * @param userId ID del usuario a consultar
     * @return Datos del usuario
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            // TODO: Implementar obtención de usuario por ID
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario obtenido exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener usuario");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
