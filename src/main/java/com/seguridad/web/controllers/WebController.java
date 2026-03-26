package com.seguridad.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seguridad.web.Dto.UserDto;
import com.seguridad.web.models.Usuario;
import com.seguridad.web.services.UsuarioServiceImpl;

import jakarta.validation.Valid;

@Controller
public class WebController {

	private static final Logger logger = LoggerFactory.getLogger(WebController.class);

	@Autowired
	private UsuarioServiceImpl usuarioServiceImpl;

	@GetMapping("/")
	public String index() {
		return "index";
	}

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

	@GetMapping("/registro")
	public String mostrarRegistroForm(Model model) {
		UserDto userDto = new UserDto();
		model.addAttribute("usuario", userDto);
		return "registro";
	}

	@PostMapping("/registro/guardar")
	public String registroGuardar(@Valid @ModelAttribute("usuario") UserDto userDto, BindingResult result,
			Model model) {
		logger.info("Intento de registro de usuario: {}", userDto.getEmail());

		if (result.hasErrors()) {
			logger.warn("Errores de validación en registro: {}", result.getFieldErrors());
			model.addAttribute("usuario", userDto);
			return "registro";
		}

		try {
			Usuario usuarioExistente = usuarioServiceImpl.findByEmail(userDto.getEmail());
			if (usuarioExistente != null && usuarioExistente.getEmail() != null
					&& !usuarioExistente.getEmail().isEmpty()) {
				logger.warn("Intento de registro con email duplicado: {}", userDto.getEmail());
				result.rejectValue("email", "email.duplicated",
						"Ya existe una cuenta registrada con el mismo correo electrónico");
				model.addAttribute("usuario", userDto);
				return "registro";
			}

			Usuario usuarioGuardado = usuarioServiceImpl.saveUser(userDto);
			logger.info("Usuario registrado exitosamente: {} (ID: {})", usuarioGuardado.getEmail(),
					usuarioGuardado.getId());

			return "redirect:/login";

		} catch (Exception e) {
			logger.error("Error al registrar usuario: {} - {}", userDto.getEmail(), e.getMessage(), e);
			result.rejectValue("email", "register.error", "Error al registrar: " + e.getMessage());
			model.addAttribute("usuario", userDto);
			return "registro";
		}
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
