package org.example.filecloud.service;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.filecloud.controller.repository.UserRepository;
import org.example.filecloud.dao.User;
import org.example.filecloud.dto.LoginUser;
import org.example.filecloud.dto.RegNewUser;
import org.example.filecloud.security.CurrentUserService;
import org.example.filecloud.security.SecurityConfig;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    PasswordEncoder passwordEncoder;
    CurrentUserService currentUserService;
    UserRepository userRepository;

    public AuthService(PasswordEncoder passwordEncoder, CurrentUserService currentUserService, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
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

    }

    @Transactional
    public boolean loginUser(LoginUser dto){
        if (dto.username() == null || dto.password() == null) {
            throw new IllegalArgumentException("Користувач не ввів нік або пароль");
        }

        User user = userRepository.findByUsername(dto.username());

        if (user == null) {
            throw new IllegalArgumentException(
                    "Username або password неправильні"
            );
        }

        return true;
    }


}
