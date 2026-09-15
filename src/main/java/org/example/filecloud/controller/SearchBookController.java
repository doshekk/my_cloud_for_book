package org.example.filecloud.controller;

import org.example.filecloud.dto.searchBooks.*;
import org.example.filecloud.service.parse.ParserService;
import org.example.filecloud.service.SearchBookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SearchBookController {

    private final SearchBookService searchBookService;
    private final ParserService parserService;

    public SearchBookController(SearchBookService searchBookService, ParserService parserService) {
        this.searchBookService = searchBookService;
        this.parserService = parserService;
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

        List<SeriasResponse> books =
                parserService.parseSearchSerias(series);

        for (SeriasResponse a : books){
            System.out.println();
        }

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
}