package com.skillnest.web.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotEmpty(message = "username no puede estar vacio")
    private String username;
    
    @NotEmpty(message = "Role no puede estar vacio")
    private String role;
    
    @NotEmpty(message = "Email no puede estar vacio")
    @Email(message = "Email debe ser valido")
    private String email;
    
    @NotEmpty(message = "Password no puede estar vacio")
    @Size(min = 6, message = "Password no puede ser menor a 6 caracteres")
    private String password;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
    
}
