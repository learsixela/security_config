package com.seguridad.web.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDto {
    
    private Long id;
    
    @NotEmpty(message = "username no puede estar vacio")
    private String username;
    
    // Opcional: se asigna automáticamente en el servicio (ADMIN si es primero, USER si no)
    private String role;
    
    @NotEmpty(message = "Email no puede estar vacio")
    @Email(message = "Email debe ser valido")
    private String email;
    
    @NotEmpty(message = "Password no puede estar vacio")
    @Size(min = 6, message = "Password no puede ser menor a 6 caracteres")
    private String password;

    // Constructor sin argumentos
    public UserDto() {
    }

    // Constructor con username, email, password
    public UserDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Constructor con todos los parámetros
    public UserDto(Long id, String username, String role, String email, String password) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // toString
    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;

        if (id != null ? !id.equals(userDto.id) : userDto.id != null) return false;
        if (username != null ? !username.equals(userDto.username) : userDto.username != null) return false;
        if (role != null ? !role.equals(userDto.role) : userDto.role != null) return false;
        if (email != null ? !email.equals(userDto.email) : userDto.email != null) return false;
        return password != null ? password.equals(userDto.password) : userDto.password == null;
    }

    // hashCode
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
