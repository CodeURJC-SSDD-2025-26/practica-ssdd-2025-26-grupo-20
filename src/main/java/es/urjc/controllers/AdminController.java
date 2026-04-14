package es.urjc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.urjc.services.UserService;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    // Panel principal (Dashboard)
    @GetMapping("/admin")
    public String adminPanel(@AuthenticationPrincipal UserDetails currentUser, Model model) {
            userService.findByUsername(currentUser.getUsername()).ifPresent(u -> {
            model.addAttribute("adminName", u.getFirstName());
            model.addAttribute("adminId", u.getId());
        
            model.addAttribute("hasAdminAvatar", u.getAvatarImage() != null);
            model.addAttribute("adminUser", u);  // ← Añade el objeto completo
        });
        return "admin/admin";
    }
}