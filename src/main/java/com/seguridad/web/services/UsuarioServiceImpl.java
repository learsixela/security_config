package com.seguridad.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.seguridad.web.Dto.UserDto;
import com.seguridad.web.models.Usuario;
import com.seguridad.web.repositories.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UserDetailsService, UserService {

	private final PasswordEncoder passwordEncoder;

	public UsuarioServiceImpl(PasswordEncoder passwordEncoder) {
		this.passwordEncoder= passwordEncoder;
	}
	
    @Autowired
    private UsuarioRepository usuarioRepo;

    /**
     * Implementación de UserDetailsService
     * Busca por email (no por username) porque SecurityConfig usa .usernameParameter("email")
     * @param email Email del usuario (recibido como parámetro "email" del formulario)
     * @return UserDetails para la autenticación
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepo.findByEmail(email)
    			.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return User.builder()
            .username(usuario.getEmail())      // Usar email como username en UserDetails
            .password(usuario.getPassword())
            .roles(usuario.getRole())
            .build();
    }


    @Override
    public Usuario getUserByUsername(String username) {
        return usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

	@Override
	public Usuario saveUser(UserDto userDto) {
		 Usuario usuario = new Usuario();
		List<Usuario> usuarios = usuarioRepo.findAll();
		if (usuarios.isEmpty()) {
			usuario.setRole("ADMIN");//(userDto.getRole());
		}else {
			usuario.setRole("USER");
		}
        usuario.setUsername(userDto.getUsername());
        usuario.setEmail(userDto.getEmail());
        // encriptar password usando spring security bcrypt
        usuario.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        return usuarioRepo.save(usuario);
		
	}

	@Override
	public Usuario findByEmail(String email) {
		return usuarioRepo.findByEmail(email).orElse(null);
	}

	@Override
	public List<UserDto> findAllUsers() {
		 List<Usuario> usuario = usuarioRepo.findAll();
		 
		 return usuario.stream()
	                .map(this::mapToUserDto)
	                .collect(Collectors.toList());
	}
	
   private UserDto mapToUserDto(Usuario usuario) {
        UserDto userDto = new UserDto();
        userDto.setId(usuario.getId());
        userDto.setUsername(usuario.getUsername());
        userDto.setRole(usuario.getRole());
        userDto.setEmail(usuario.getEmail());
        return userDto;
   }
}
