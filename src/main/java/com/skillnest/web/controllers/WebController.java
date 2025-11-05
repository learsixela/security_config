package com.skillnest.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.skillnest.web.Dto.UserDto;
import com.skillnest.web.models.Usuario;
import com.skillnest.web.services.UsuarioServiceImpl;

import jakarta.validation.Valid;

@Controller
public class WebController {
	
	@Autowired
	UsuarioServiceImpl usuarioServiceImpl;

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	//@GetMapping(value= {"/","/login"})
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	@PostMapping("/login")
	public String ingreso() {
		return "home";
	}
	
	@GetMapping("/home")
	public String home(Model model) {
		model.addAttribute("usuarios", usuarioServiceImpl.findAllUsers());
		
		return "home";
	}
	
    @GetMapping("registro")
    public String mostrarRegistroForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("usuario", user);
        return "registro";
    }
	
    @PostMapping("/registro/guardar")
    public String registro_guardar(@Valid @ModelAttribute("user") UserDto userDto,
                              BindingResult result,
                              Model model) {
        Usuario existe_usuario = usuarioServiceImpl.findByEmail(userDto.getEmail());

        if (existe_usuario != null && existe_usuario.getEmail() != null && !existe_usuario.getEmail().isEmpty()) {
            result.rejectValue("email", null,
                    "Ya existe una cuenta registrada con el mismo correo electrónico");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "registro";
        }

        usuarioServiceImpl.saveUser(userDto);
        return "redirect:/login";
    }
    
	@GetMapping("/admin/detalle")
	public String detalleAdmin(Model model) {
		model.addAttribute("usuarios", usuarioServiceImpl.findAllUsers());
		return "admin";
	}
	
	@GetMapping("/user/detalle")
	public String detalleUser() {
		return "detalle_user";
	}
	
	
}
