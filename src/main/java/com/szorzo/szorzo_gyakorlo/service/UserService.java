package com.szorzo.szorzo_gyakorlo.service;

import com.szorzo.szorzo_gyakorlo.model.User;
import com.szorzo.szorzo_gyakorlo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String password) {
        // Létezés ellenőrzése az existsByUsername metódussal
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User already exists");
        }

        // Új felhasználó létrehozása
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Jelszó hash-elése
        return userRepository.save(user);
    }
}
