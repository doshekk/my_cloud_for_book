package org.example.filecloud.service.parse;

import org.example.filecloud.dto.searchBooks.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParserService {
    private final ParseAuthorPage authorService;
    private final ParserBook bookService;

    public ParserService(ParseAuthorPage authorService, ParserBook bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }


    // збір інформації про книги при пошуку
    public List<BookResponse> parseSearchBook(String html) {
        Document document = Jsoup.parse(html);
        Elements books = document.select("h3 + ul > li");
        List<BookResponse> result = new ArrayList<>();

        for (Element book : books) {

            Element bookElement = book.selectFirst("a[href^=/b/]");
            Element authorElement = book.selectFirst("a[href^=/a/]");

            if (bookElement == null || authorElement == null) {
                continue;
            }

            String title = bookElement.text();
            String bookUrl = bookElement.attr("href");
            Long bookId = Long.parseLong(
                    bookUrl.substring(bookUrl.lastIndexOf("/") + 1)
            );

            String author = authorElement.text();
            String authorUrl = authorElement.attr("href");
            Long authorId = Long.parseLong(
                    authorUrl.substring(authorUrl.lastIndexOf("/") + 1)
            );


            BookResponse dashbordResonse = new BookResponse(
                    title,
                    bookId,
                    author,
                    authorId);
            result.add(dashbordResonse);
        }
        return result;
    }

    //збір інформації про серій книг при пошуку
    public List<SeriasResponse> parseSearchSerias(String html){
        Document document = Jsoup.parse(html);
        Elements elements = document.select("h3 + ul > li");
        List<SeriasResponse> seriasResponses = new ArrayList<>();

        for (Element e : elements){
            Element mainElement = e.selectFirst("a[href^=/sequence/]");

            if (mainElement == null || mainElement == null) {
                continue;
            }

            String title = mainElement.text();
            System.out.println(title);
            String uri =  mainElement.attr("href");
            Long id = Long.parseLong(
                    uri.substring(uri.lastIndexOf("/") + 1)
            );

            SeriasResponse response = new SeriasResponse(title, id);
            seriasResponses.add(response);
        }
        return seriasResponses;
    }

    // збір інформації про авторів при пошуку
    public List<AuthorsResponse> parseSearchAuthors(String html){

        Document document = Jsoup.parse(html);
        Elements elements = document.select("#main > ul > li");

        List<AuthorsResponse> authors = new ArrayList<>();

        for (Element e : elements) {
            if (e == null || e == null) {
                continue;
            }
            Element author = e.selectFirst("a[href^=/a/]");
            String name = author.text();
            String uri = author.attr("href");
            Long id = Long.parseLong(
                    uri.substring(uri.lastIndexOf("/") + 1)
            );
            AuthorsResponse authorsResponse = new AuthorsResponse(
                    name,
                    id
            );
            authors.add(authorsResponse);
        }

        return authors;
    }

    public BookInfoResponse getBook(String html){
        return bookService.parseBook(html);
    }

    public List<BookResponse> getBooksFromSerias(String html){
        return bookService.parseSeriasToBook(html);
    }

    public AuthorResponse getAuthor(String html){
       return authorService.getAuthorDTO(html);
    };
}
