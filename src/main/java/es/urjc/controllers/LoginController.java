package es.urjc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // 1. Pantalla para elegir Usuario o Admin
    @GetMapping("/login")
    public String showLoginSelection() {
        return "login";
    }

    // 2. Pantalla del formulario de Usuario
    @GetMapping("/loginuser")
    public String showUserLogin() {
        return "loginuser";
    }

    // 3. Pantalla del formulario de Admin
    @GetMapping("/loginadmin")
    public String showAdminLogin() {
        return "loginadmin";
    }
}