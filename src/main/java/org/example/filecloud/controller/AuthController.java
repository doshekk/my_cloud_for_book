package org.example.filecloud.controller;

import org.example.filecloud.dto.RegNewUser;
import org.example.filecloud.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }
    @GetMapping("/register")
    public String getRegPage(){
        return "register";
    }

    @PostMapping("/register")
    public String regUser(@ModelAttribute RegNewUser DTO){
        System.out.println(DTO.username());
        System.out.println(DTO.password());
        authService.regNewUser(DTO);
        return "home";
    }


}
