package com.skillnest.web.services;

import java.util.List;

import com.skillnest.web.Dto.UserDto;
import com.skillnest.web.models.Usuario;

public interface UserService {
    void saveUser(UserDto userDto);
    Usuario findByEmail(String email);
    List<UserDto> findAllUsers();
}
