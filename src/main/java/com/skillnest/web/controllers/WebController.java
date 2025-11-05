package com.skillnest.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	//@GetMapping(value= {"/","/login"})
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@GetMapping("/home")
	public String home() {
		return "home";
	}
	
	@GetMapping("/admin/detalle")
	public String detalleAdmin() {
		return "admin";
	}
	
	@GetMapping("/user/detalle")
	public String detalleUser() {
		return "detalle_user";
	}
	
	
}
