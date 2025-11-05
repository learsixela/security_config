package com.skillnest.web.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillnest.web.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);
}
