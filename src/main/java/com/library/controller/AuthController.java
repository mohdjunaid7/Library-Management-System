package com.library.controller;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {
        Optional<User> userOpt = userService.login(email, password);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        User user = userOpt.get();
        session.setAttribute("user", user);

        if (user.getRole() == Role.LIBRARIAN) {
            return "redirect:/librarian/dashboard";
        }
        return "redirect:/member/dashboard";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                            @RequestParam String email,
                            @RequestParam String password,
                            @RequestParam String phone,
                            Model model) {
        try {
            userService.registerMember(name, email, password, phone);
            model.addAttribute("success", "Registration successful! Please log in.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
