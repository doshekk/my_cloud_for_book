package org.example.filecloud.controller;

import org.example.filecloud.dao.CoverFileMetadata;
import org.example.filecloud.dao.FileMetadata;
import org.example.filecloud.dao.book.Book;
import org.example.filecloud.dao.book.BookGenre;
import org.example.filecloud.dao.book.ReadStatus;
import org.example.filecloud.dto.book.BookUpdateRequest;
import org.example.filecloud.dto.dashbord.DashboardView;
import org.example.filecloud.repository.BookRepository;
import org.example.filecloud.service.BooksService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.example.filecloud.security.CurrentUserService;
import org.example.filecloud.service.dashboard.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public ResponseEntity<Resource> getBookFile(
            @PathVariable Long id
    ) throws IOException {

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

        Path path = Paths.get(
                metadata.getFileStoragePath()
        );

        if (!Files.exists(path)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Файл не знайдено на диску"
            );
        }

        System.out.println("========== BOOK FILE ==========");
        System.out.println("Path: " + path);
        System.out.println("Name: " + path.getFileName());
        System.out.println("Size: " + Files.size(path) + " bytes");
        System.out.println("Type: " + metadata.getFleType());
        System.out.println("===============================");

        Resource resource = new InputStreamResource(
                Files.newInputStream(path)
        );

        MediaType mediaType = switch (metadata.getFleType()) {

            case epub -> MediaType.parseMediaType(
                    "application/epub+zip"
            );

            case pdf -> MediaType.APPLICATION_PDF;

            case fb2 -> MediaType.TEXT_XML;
        };

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" +
                                path.getFileName() +
                                "\""
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

    @GetMapping("/books/{id}/edit")
    public String editBookPage(
            @PathVariable Long id,
            Model model,
            Authentication authentication
    ) {
        Book book = booksService.getBookById(id);

        model.addAttribute("book", book);
        model.addAttribute("genres", BookGenre.values());
        model.addAttribute("readStatuses", ReadStatus.values());
        model.addAttribute("username", authentication.getName());

        return "edit-book";
    }

    @PostMapping(
            value = "/books/{id}/edit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String editBook(
            @PathVariable Long id,
            @ModelAttribute BookUpdateRequest bookUpdate
    ) {
        booksService.editBook(id, bookUpdate);

        return "redirect:/books";
    }

    @GetMapping("/books/{id}/download")
    public ResponseEntity<Resource> downloadBook(@PathVariable Long id) throws IOException {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Книгу не знайдено"
                ));

        FileMetadata metadata = book.getFileMetadata();

        if (metadata == null || metadata.getFileStoragePath() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Файл книги не знайдено"
            );
        }

        Path path = Paths.get(metadata.getFileStoragePath());

        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Файл не існує"
            );
        }

        Resource resource = new FileSystemResource(path);

        String extension = switch (metadata.getFleType()) {
            case epub -> ".epub";
            case pdf -> ".pdf";
            case fb2 -> ".fb2";
        };

        String contentType = switch (metadata.getFleType()) {
            case epub -> "application/epub+zip";
            case pdf -> "application/pdf";
            case fb2 -> "application/xml";
        };

        String fileName = metadata.getName();

        if (!fileName.toLowerCase().endsWith(extension)) {
            fileName += extension;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(Files.size(path))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\""
                )
                .body(resource);
    }
}
