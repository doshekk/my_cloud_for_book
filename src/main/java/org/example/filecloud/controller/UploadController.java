package org.example.filecloud.controller;

import org.example.filecloud.dto.book.BookUploadRequest;
import org.example.filecloud.service.BooksService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UploadController {
    BooksService booksService;

    public UploadController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("/books/upload")
    public String getPage(){return "upload";}

    @PostMapping("/books/upload")
    public String addBook(@ModelAttribute BookUploadRequest request){
        booksService.addBook(request);
        return "redirect:/books";
    }
}
