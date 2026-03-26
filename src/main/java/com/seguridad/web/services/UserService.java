package com.seguridad.web.services;

import java.util.List;

import com.seguridad.web.Dto.UserDto;
import com.seguridad.web.models.Usuario;

public interface UserService {
    Usuario saveUser(UserDto userDto);
    Usuario findByEmail(String email);
    List<UserDto> findAllUsers();
    Usuario getUserByUsername(String username);
}
