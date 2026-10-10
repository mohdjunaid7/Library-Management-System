package com.library.controller;

import com.library.entity.User;
import com.library.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MemberController {

    @Autowired private UserService userService;

    // ---------- Librarian: manage member accounts ----------
    @GetMapping("/librarian/members")
    public String manageMembers(Model model) {
        model.addAttribute("members", userService.getAllMembers());
        return "librarian-members";
    }

    @GetMapping("/librarian/members/new")
    public String newMemberForm(Model model) {
        model.addAttribute("member", new User());
        return "member-form";
    }

    @PostMapping("/librarian/members/save")
    public String saveMember(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String password,
                              @RequestParam String phone,
                              Model model) {
        try {
            userService.createMember(name, email, password, phone);
            return "redirect:/librarian/members";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "member-form";
        }
    }

    @GetMapping("/librarian/members/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        userService.deleteMember(id);
        return "redirect:/librarian/members";
    }

    // ---------- Member: update own profile ----------
    @GetMapping("/member/profile")
    public String profilePage(HttpSession session, Model model) {
        model.addAttribute("user", session.getAttribute("user"));
        return "member-profile";
    }

    @PostMapping("/member/profile")
    public String updateProfile(@RequestParam String name,
                                 @RequestParam String phone,
                                 @RequestParam(required = false) String password,
                                 HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        User updated = userService.updateProfile(sessionUser.getId(), name, phone, password);
        session.setAttribute("user", updated); // keep session in sync
        model.addAttribute("user", updated);
        model.addAttribute("success", "Profile updated successfully");
        return "member-profile";
    }
}
