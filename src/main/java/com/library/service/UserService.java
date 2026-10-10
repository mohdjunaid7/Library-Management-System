package com.library.service;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /** Member self-registration (used by the public "Register" page). */
    public User registerMember(String name, String email, String password, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        User user = new User(name, email, PasswordUtil.hash(password), phone, Role.MEMBER);
        user.setMembershipId(generateMembershipId());
        return userRepository.save(user);
    }

    /** Librarian creating a member account from the admin dashboard. */
    public User createMember(String name, String email, String password, String phone) {
        return registerMember(name, email, password, phone);
    }

    public Optional<User> login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && PasswordUtil.matches(rawPassword, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public List<User> getAllMembers() {
        return userRepository.findByRole(Role.MEMBER);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    public User updateProfile(Long id, String name, String phone, String password) {
        User user = getById(id);
        user.setName(name);
        user.setPhone(phone);
        if (password != null && !password.isBlank()) {
            user.setPassword(PasswordUtil.hash(password));
        }
        return userRepository.save(user);
    }

    public void deleteMember(Long id) {
        userRepository.deleteById(id);
    }

    private String generateMembershipId() {
        long count = userRepository.findByRole(Role.MEMBER).size() + 1;
        return String.format("MEM%04d", count);
    }
}
