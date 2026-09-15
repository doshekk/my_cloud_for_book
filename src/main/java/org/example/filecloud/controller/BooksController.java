package org.example.filecloud.controller;

import org.example.filecloud.dao.CoverFileMetadata;
import org.example.filecloud.dao.FileMetadata;
import org.example.filecloud.dao.book.Book;
import org.example.filecloud.dto.dashbord.DashboardView;
import org.example.filecloud.repository.BookRepository;
import org.example.filecloud.service.BooksService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.example.filecloud.security.CurrentUserService;
import org.example.filecloud.service.dashboard.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;


@Controller
public class BooksController {
    DashboardService dashboardService;
    CurrentUserService currentUserService;
    BookRepository bookRepository;
    BooksService booksService;

    public BooksController(DashboardService dashboardService, CurrentUserService currentUserService, BookRepository bookRepository, BooksService booksService) {
        this.dashboardService = dashboardService;
        this.currentUserService = currentUserService;
        this.bookRepository = bookRepository;
        this.booksService = booksService;
    }


    @GetMapping("/books")
    public String getPage(Model model){
        DashboardView dashboardView = dashboardService.getInfoAboutUser();
        List<Book> books = dashboardView.books().stream()
                .sorted(Comparator.comparing(Book::getAddTime))
                .toList();

        model.addAttribute("books",books);
        model.addAttribute("username", currentUserService.getCurrentUser().getUserName());

        return "books";
    }


    @PostMapping("/books/{id}/favorite")
    public String setFavorite(@PathVariable Long id){
        booksService.setFavorite(id);
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable Long id){
        booksService.deleteBook(id);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}/file")
    public ResponseEntity<Resource> getBookFile(@PathVariable Long id) throws IOException {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Книгу не знайдено"
                ));

        FileMetadata metadata = book.getFileMetadata();

        if (metadata == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Файл книги не знайдено"
            );
        }

        Path path = Paths.get(metadata.getFileStoragePath());

        if (!Files.exists(path)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Файл не знайдено на диску"
            );
        }

        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + path.getFileName() + "\""
                )
                .body(resource);
    }

    @GetMapping("/books/{id}/cover")
    public ResponseEntity<Resource> getBookCover(@PathVariable Long id) throws IOException {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Книгу не знайдено"
                ));

        CoverFileMetadata metadata = book.getCoverFileMetadata();

        if (metadata == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Обкладинку не знайдено"
            );
        }

        Path path = Paths.get(metadata.getFileStoragePath());

        if (!Files.exists(path)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Файл обкладинки не знайдено"
            );
        }

        Resource resource = new UrlResource(path.toUri());

        String contentType = Files.probeContentType(path);

        MediaType mediaType = contentType != null
                ? MediaType.parseMediaType(contentType)
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + path.getFileName() + "\""
                )
                .body(resource);
    }
}
