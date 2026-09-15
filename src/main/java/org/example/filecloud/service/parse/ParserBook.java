package org.example.filecloud.service.parse;

import org.example.filecloud.dto.searchBooks.BookInfoResponse;
import org.example.filecloud.dto.searchBooks.BookResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParserBook {
    // збір детальної інформації про книгу на її сторінці
    public BookInfoResponse parseBook(String html) {

        Document document = Jsoup.parse(html);

        Element element = document.selectFirst("#main");

        if (element == null) {
            throw new IllegalStateException("#main не знайдено");
        }

        Elements authorElements = element.select("a[href^=/a/]");

        List<String> authors = new ArrayList<>();
        List<Long> authorsId = new ArrayList<>();

        for (Element authorElement : authorElements) {

            String authorUri = authorElement.attr("href");

            if (!authorUri.matches("/a/\\d+")) {
                continue;
            }

            authors.add(authorElement.text());

            Long authorId = Long.parseLong(
                    authorUri.substring(authorUri.lastIndexOf("/") + 1)
            );

            authorsId.add(authorId);
        }

        String title = element.select(".title").text();

        String description = element.select("p").text();

        Element cover = element.selectFirst(
                "img[alt='Cover image']"
        );

        String coverUrl = null;

        if (cover != null) {

            String coverUri = cover.attr("src");

            coverUrl = "https://flibusta.site" + coverUri;
        }

        return new BookInfoResponse(
                title,
                description,
                authors,
                authorsId,
                coverUrl
        );
    }

    //збір інформації про книгу з серії книг
    public List<BookResponse> parseSeriasToBook(String html) {

        Document document = Jsoup.parse(html);

        Element main = document.selectFirst("#main");

        List<BookResponse> list = new ArrayList<>();

        if (main == null) {
            return list;
        }

        // тільки основні посилання на книги /b/123
        Elements books = main.select("a[href~=/b/\\d+$]");

        for (Element book : books) {

            String bookTitle = book.text();
            String bookUri = book.attr("href");

            String bookIdString =
                    bookUri.substring(bookUri.lastIndexOf("/") + 1);

            Long bookId = Long.parseLong(bookIdString);

            // Шукаємо автора після цієї книги
            Element author = null;

            Element current = book.nextElementSibling();

            while (current != null) {

                // Якщо зустріли наступну книгу — автора для поточної не знайшли
                if (current.is("a")
                        && current.attr("href").matches("/b/\\d+")) {
                    break;
                }

                // Знайшли автора
                if (current.is("a")
                        && current.attr("href").matches("/a/\\d+")) {

                    author = current;
                    break;
                }

                current = current.nextElementSibling();
            }

            // Якщо автора немає
            if (author == null) {
                continue;
            }

            String authorName = author.text();
            String authorUri = author.attr("href");

            String authorIdString =
                    authorUri.substring(authorUri.lastIndexOf("/") + 1);

            Long authorId = Long.parseLong(authorIdString);

            BookResponse bookResponse = new BookResponse(
                    bookTitle,
                    bookId,
                    authorName,
                    authorId
            );

            list.add(bookResponse);
        }

        return list;
    }


}
