package com.library.config;

import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.UserRepository;
import com.library.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Creates a default Librarian account on first run so there's a way to log in
 * without needing direct database access.
 *
 * Default login -> email: admin@library.com | password: admin123
 * (Please change this password after first login, or edit it below before running.)
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@library.com")) {
            User librarian = new User(
                    "Admin Librarian",
                    "admin@library.com",
                    PasswordUtil.hash("admin123"),
                    "0000000000",
                    Role.LIBRARIAN
            );
            userRepository.save(librarian);
            System.out.println(">>> Default librarian account created: admin@library.com / admin123");
        }
    }
}
