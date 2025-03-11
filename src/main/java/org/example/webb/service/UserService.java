package org.example.webb.service;

import jakarta.transaction.Transactional;
import org.example.webb.entity.User;
import org.example.webb.repository.UserRepository;
import org.example.webb.util.PasswordUtil;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User addUser() {
        final String salt = PasswordUtil.generateSalt();
        final String password = PasswordUtil.generateStrongPassword();
        final String username = String.format("user%d", userRepository.count() + 1);

        User user = new User(username, PasswordUtil.hashPassword(password, salt), salt);
        userRepository.save(user);
        return user;
    }
}
