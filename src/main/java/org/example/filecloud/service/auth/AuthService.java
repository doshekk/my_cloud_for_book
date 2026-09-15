package org.example.filecloud.service.auth;

import org.example.filecloud.repository.UserRepository;
import org.example.filecloud.dao.user.User;
import org.example.filecloud.dto.user.LoginUser;
import org.example.filecloud.dto.user.RegNewUser;
import org.example.filecloud.security.CurrentUserService;
import org.example.filecloud.service.FileStorageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    PasswordEncoder passwordEncoder;
    CurrentUserService currentUserService;
    UserRepository userRepository;
    FileStorageService fileStorageService;

    public AuthService(PasswordEncoder passwordEncoder, CurrentUserService currentUserService, UserRepository userRepository, FileStorageService fileStorageService) {
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public void regNewUser(RegNewUser userDTO){
        if (userDTO.username() == null && userDTO.password() == null) {
            throw new IllegalArgumentException("Користувач не ввів нік або пароль");
        }

        if (userRepository.existsByUsername(userDTO.username())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        String passwordHash;
        passwordHash = passwordEncoder.encode(userDTO.password());

        user.setUserName(userDTO.username());
        user.setHashPassword(passwordHash);
        userRepository.save(user);

        fileStorageService.createUserFolder(user);

    }
}
