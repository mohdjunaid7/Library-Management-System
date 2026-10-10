package com.library.controller;

import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.NotificationService;
import com.library.service.TransactionService;
import com.library.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired private BookService bookService;
    @Autowired private UserService userService;
    @Autowired private TransactionService transactionService;
    @Autowired private NotificationService notificationService;

    @GetMapping("/librarian/dashboard")
    public String librarianDashboard(HttpSession session, Model model) {
        model.addAttribute("totalBooks", bookService.getAllBooks().size());
        model.addAttribute("totalMembers", userService.getAllMembers().size());
        model.addAttribute("totalTransactions", transactionService.getAllTransactions().size());
        model.addAttribute("user", session.getAttribute("user"));
        return "librarian-dashboard";
    }

    @GetMapping("/member/dashboard")
    public String memberDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("history", transactionService.getHistoryForMember(user.getId()));
        model.addAttribute("notifications", notificationService.getForMember(user.getId()));
        return "member-dashboard";
    }
}
