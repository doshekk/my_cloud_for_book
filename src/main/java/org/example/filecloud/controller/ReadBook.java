package org.example.filecloud.controller;

import org.example.filecloud.dao.FileType;
import org.example.filecloud.dao.book.Book;
import org.example.filecloud.service.BooksService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ReadBook {
    BooksService booksService;

    public ReadBook(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("/books/{id}/read")
    public String readBook(
            @PathVariable Long id,
            Model model,
            Authentication authentication
    ) {

        Book book = booksService.getBookById(id);

        model.addAttribute("book", book);
        model.addAttribute("username", authentication.getName());

        String fileType = "";

        if (book.getFileMetadata() != null
                && book.getFileMetadata().getFleType() != null) {

            fileType = book.getFileMetadata()
                    .getFleType()
                    .name()
                    .toLowerCase();
        }

        System.out.println(fileType);
        model.addAttribute("fileType", fileType);

        return "read-book";
    }


}
