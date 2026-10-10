package com.library.controller;

import com.library.entity.NotificationType;
import com.library.entity.User;
import com.library.service.NotificationService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NotificationController {

    @Autowired private NotificationService notificationService;
    @Autowired private UserService userService;

    // ---------- Librarian: send a notification ----------
    @GetMapping("/librarian/notifications")
    public String notificationPage(Model model) {
        model.addAttribute("members", userService.getAllMembers());
        return "librarian-notifications";
    }

    @PostMapping("/librarian/notifications/send")
    public String sendNotification(@RequestParam(required = false) Long memberId,
                                    @RequestParam String message,
                                    @RequestParam(required = false, defaultValue = "false") boolean toAll,
                                    Model model) {
        if (toAll) {
            notificationService.sendToAllMembers(message, NotificationType.GENERAL);
        } else if (memberId != null) {
            notificationService.sendToMember(memberId, message, NotificationType.GENERAL);
        }
        model.addAttribute("success", "Notification sent successfully");
        model.addAttribute("members", userService.getAllMembers());
        return "librarian-notifications";
    }

    // ---------- Member: mark a notification as read ----------
    @PostMapping("/member/notifications/read/{id}")
    public String markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/member/dashboard";
    }
}
