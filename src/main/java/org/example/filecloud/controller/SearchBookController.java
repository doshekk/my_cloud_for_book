package org.example.filecloud.controller;

import org.example.filecloud.dao.FileType;
import org.example.filecloud.dao.book.BookGenre;
import org.example.filecloud.dao.book.ReadStatus;
import org.example.filecloud.dao.user.User;
import org.example.filecloud.dto.SearchedBookSave;
import org.example.filecloud.dto.book.FileDTO;
import org.example.filecloud.dto.searchBooks.*;
import org.example.filecloud.security.CurrentUserService;
import org.example.filecloud.service.BooksService;
import org.example.filecloud.service.DownloadBookById;
import org.example.filecloud.service.parse.ParserService;
import org.example.filecloud.service.SearchBookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class SearchBookController {

    private final SearchBookService searchBookService;
    private final ParserService parserService;
    private final DownloadBookById downloadBook;
    private final CurrentUserService currentUserService;
    private final BooksService booksService;

    public SearchBookController(SearchBookService searchBookService, ParserService parserService, DownloadBookById downloadBook, CurrentUserService currentUserService, BooksService booksService) {
        this.searchBookService = searchBookService;
        this.parserService = parserService;
        this.downloadBook = downloadBook;
        this.currentUserService = currentUserService;
        this.booksService = booksService;
    }


    @GetMapping("/search")
    public String searchPage(Model model) {

        model.addAttribute("books", null);
        model.addAttribute("query", "");
        model.addAttribute("page", 1);
        model.addAttribute("total", 0);

        return "search-book";
    }

    @PostMapping("/search")
    public String searchQuery(
            @RequestParam String query,
            Model model) {
        String bookResponse = searchBookService.searchBooks(query, 1);
        String seriesResponse = searchBookService.seriesBooks(query, 1);
        String authorResponse = searchBookService.searchAuthors(query, 1);


        List<BookResponse> books = parserService.parseSearchBook(bookResponse);
        List<SeriasResponse> serias = parserService.parseSearchSerias(seriesResponse);
        List<AuthorsResponse> authors = parserService.parseSearchAuthors(authorResponse);

        model.addAttribute("totalBooks", books.size());
        model.addAttribute("totalSerias", serias.size());
        model.addAttribute("totalAuthors", authors.size());

        model.addAttribute("books", books);
        model.addAttribute("serias", serias);
        model.addAttribute("authors", authors);


        return "search-book";
    }

    @GetMapping("/serias/{id}")
    public String getSerias(
            @PathVariable Long id,
            Model model) {

        String series = searchBookService.series(id);

        List<BookResponse> books =
                parserService.getBooksFromSerias(series);

        model.addAttribute("books", books);
        model.addAttribute("query", id);
        model.addAttribute("total", books.size());

        return "search-serias";
    }

    @GetMapping("/book/{id}")
    public String getBook(@PathVariable Long id, Model model){
        String html = searchBookService.book(id, 1);
        BookInfoResponse book = parserService.getBook(html);

        model.addAttribute("book",book);

        return "book-info";
    }

    @GetMapping("/author/{id}")
    public String getAuthor(@PathVariable Long id, Model model) {

        String html = searchBookService.author(id);

        AuthorResponse author = parserService.getAuthor(html);

        int bookCount = author.seriesList()
                .stream()
                .mapToInt(series -> series.books().size())
                .sum();

        model.addAttribute("author", author);
        model.addAttribute("series", author.seriesList());
        model.addAttribute("bookCount", bookCount);

        return "author";
    }

    @GetMapping("/book/{id}/add")
    public String addBook(@PathVariable Long id, Model model) {

        String html = searchBookService.book(id, 1);

        BookInfoResponse book = parserService.getBook(html);

        SearchedBookSave dto = new SearchedBookSave(
                id,
                book.title(),
                book.description(),
                book.authors(),
                null,       // readStatus
                false,      // isFavorite
                List.of()   // genres
        );

        model.addAttribute("book", dto);

        model.addAttribute("genres", BookGenre.values());

        return "add-searched-book";
    }

    @PostMapping("/book/{id}/save")
    public String saveSearchedBookEpub(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(name = "genres", required = false)
            List<BookGenre> genres,
            @RequestParam String authors,
            @RequestParam(required = false) ReadStatus readStatus,
            @RequestParam(defaultValue = "false") boolean isFavorite
    ) throws IOException {


        if (genres == null) {
            genres = new ArrayList<>();
        }

        List<String> authorsList = Arrays.stream(authors.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        SearchedBookSave bookDTO = new SearchedBookSave(
                id,
                title,
                description,
                authorsList,
                readStatus,
                isFavorite,
                genres
        );


        // завантаження файлів
        String html = searchBookService.book(id, 1);
        BookInfoResponse book = parserService.getBook(html);

        byte[] bookFile = downloadBook.downloadBook(id);

        byte[] coverFile ;

        try {
            coverFile = downloadBook.downloadCover(book.coverUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FileDTO fileDTO = new FileDTO(bookFile, coverFile);
        FileType type = FileType.epub;
        booksService.addSearchedBook(bookDTO, fileDTO, type);

        return "redirect:/books";
    }

}