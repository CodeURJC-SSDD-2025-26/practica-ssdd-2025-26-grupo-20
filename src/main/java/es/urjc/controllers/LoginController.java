package es.urjc.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Nombre de usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("logout", "Has sido desconectado exitosamente");
        }

        return "login";
    }

    // 2. Pantalla del formulario de Usuario
    @GetMapping("/loginuser")
    public String loginUser() {
        return "loginuser";
    }

    // 3. Pantalla del formulario de Admin
    @GetMapping("/loginadmin")
    public String loginAdmin() {
        return "loginadmin";
    }
}