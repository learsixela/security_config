package com.skillnest.web.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillnest.web.Dto.UserDto;
import com.skillnest.web.models.Usuario;
import com.skillnest.web.repositories.UsuarioRepository;

@Service
public class UsuarioServiceImpl implements UserDetailsService, UserService {

	private final PasswordEncoder passwordEncoder;

	public UsuarioServiceImpl(PasswordEncoder passwordEncoder) {
		this.passwordEncoder= passwordEncoder;
	}
	
    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("No encontrado"));

        return User.builder()
            .username(usuario.getUsername())
            .password(usuario.getPassword())
            .roles(usuario.getRole())
            .build();
    }

	@Override
	public void saveUser(UserDto userDto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(userDto.getUsername());
        usuario.setRole(userDto.getRole());
        usuario.setEmail(userDto.getEmail());
        // encriptar password usando spring security bcrypt
        usuario.setPassword(passwordEncoder.encode(userDto.getPassword()));
        
        usuarioRepo.save(usuario);
		
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
