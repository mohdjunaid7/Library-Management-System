package com.library.service;

import com.library.entity.Notification;
import com.library.entity.NotificationType;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.NotificationRepository;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Notification> getForMember(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public void sendToMember(Long memberId, String message, NotificationType type) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        notificationRepository.save(new Notification(member, message, type));
    }

    /** Broadcast a notification (e.g. new arrival) to every member. */
    public void sendToAllMembers(String message, NotificationType type) {
        List<User> members = userRepository.findByRole(Role.MEMBER);
        for (User member : members) {
            notificationRepository.save(new Notification(member, message, type));
        }
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
