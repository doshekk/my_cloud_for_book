package org.example.filecloud.controller;

import org.example.filecloud.security.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HomeController {
    CurrentUserService currentUserService;

    public HomeController(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @GetMapping("/")
    public String home(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute("username", currentUserService.getCurrentUser().getUserName());

        model.addAttribute("totalBooks", 12);
        model.addAttribute("readBooks", 8);
        model.addAttribute("readingBooks", 2);
        model.addAttribute("favoriteBooks", 3);

        model.addAttribute(
                "recentBooks",
                List.of()
        );

        return "home";
    }
}
