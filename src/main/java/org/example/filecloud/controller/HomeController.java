package org.example.filecloud.controller;

import org.example.filecloud.dao.book.Book;
import org.example.filecloud.dto.dashbord.DashboardView;
import org.example.filecloud.security.CurrentUserService;
import org.example.filecloud.service.dashboard.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {
    CurrentUserService currentUserService;
    DashboardService dashboardService;

    public HomeController(CurrentUserService currentUserService, DashboardService dashboardService) {
        this.currentUserService = currentUserService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String home(Model model) {
        DashboardView dashboardView = dashboardService.getInfoAboutUser();
        List<Book> books = dashboardView.books().stream()
                .sorted(Comparator.comparing(Book::getAddTime))
                .toList();

        model.addAttribute("username", currentUserService.getCurrentUser().getUserName());
        model.addAttribute("totalBooks", dashboardView.totalBooks());
        model.addAttribute("readBooks", dashboardView.readBooks());
        model.addAttribute("readingBooks", dashboardView.readNowBooks());
        model.addAttribute("favoriteBooks", dashboardView.favoriteBooks());

        model.addAttribute("recentBooks",books);

        return "home";
    }
}
