package org.example.filecloud.service.parse;

import org.example.filecloud.dto.searchBooks.AuthorResponse;
import org.example.filecloud.dto.searchBooks.SeriesBookResponse;
import org.example.filecloud.dto.searchBooks.SeriesResponse;
import org.example.filecloud.dto.searchBooks.TranslatorResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParseAuthorPage {

    // збір дто для контроллера
    public AuthorResponse getAuthorDTO(String html) {

        Document document = Jsoup.parse(html);

        String name = document
                .select("div#content-top h1.title")
                .text();

        Element authorBio = document.selectFirst("div#divabio");

        String description = "";

        if (authorBio != null) {
            Elements paragraphs = authorBio.select("p");

            StringBuilder fullText = new StringBuilder();

            for (Element p : paragraphs) {
                String text = p.text().trim();

                if (!text.isEmpty()) {
                    if (!fullText.isEmpty()) {
                        fullText.append("\n");
                    }

                    fullText.append(text);
                }
            }

            description = fullText.toString();
        }

        String imageUri = "";

        if (authorBio != null) {

            Element imgElement = authorBio.selectFirst("img");

            if (imgElement != null) {

                String src = imgElement.attr("src");

                if (!src.isBlank()) {
                    imageUri = "https://flibusta.site" + src;
                }
            }
        }

        List<SeriesResponse> seriesList = parseAuthorSeries(html);

        if (seriesList.isEmpty()) {
            seriesList = null;
        }

        return new AuthorResponse(
                name,
                description,
                seriesList,
                imageUri
        );
    }

    // парсинг серій книг та перекладачів автора
    public List<SeriesResponse> parseAuthorSeries(String html) {

        Document document = Jsoup.parse(html);

        Elements forms = document.select("form[action^='/a/']");
        Element form;
        if(forms.size() >= 2){
            form = forms.get(1);
        }else {
            form = forms.get(0);
        }
        List<SeriesResponse> result = new ArrayList<>();

        if (form == null) {
            System.out.println("FORM NOT FOUND");
            return result;
        }

        System.out.println("FORM FOUND");
        System.out.println(form);

        SeriesBuilder currentSeries = null;

        Elements links = form.select("a[href]");

        for (Element element : links) {

            String href = element.attr("href");

            // Серія
            if (isSeriesLink(element)) {

                if (currentSeries != null) {
                    result.add(currentSeries.build());
                }

                Long seriesId = extractId(href);

                String seriesName = element.text();

                currentSeries = new SeriesBuilder(
                        seriesId,
                        seriesName
                );

                continue;
            }

            // Книга
            if (isBookLink(element)) {

                if (currentSeries == null) {
                    continue;
                }

                SeriesBookResponse book =
                        parseAuthorBook(element);

                currentSeries.books.add(book);
            }
        }

        if (currentSeries != null) {
            result.add(currentSeries.build());
        }

        return result;
    }

    private SeriesBookResponse parseAuthorBook(Element bookElement) {

        Long bookId = extractId(
                bookElement.attr("href")
        );

        String title = bookElement.text();

        List<TranslatorResponse> translators =
                new ArrayList<>();

        Node current = bookElement.nextSibling();

        while (current != null) {

            if (current instanceof Element element) {

                // закінчили поточний запис книги
                if (element.is("br")) {
                    break;
                }

                if (isTranslatorLink(element)) {

                    Long translatorId =
                            extractId(element.attr("href"));

                    String translatorName =
                            element.text();

                    translators.add(
                            new TranslatorResponse(
                                    translatorId,
                                    translatorName
                            )
                    );
                }
            }

            current = current.nextSibling();
        }

        return new SeriesBookResponse(
                bookId,
                title,
                translators
        );
    }

    private boolean isSeriesLink(Element element) {

        if (!element.is("a")) {
            return false;
        }

        return element.attr("href")
                .matches("/s/\\d+");
    }
    private boolean isBookLink(Element element) {

        if (!element.is("a")) {
            return false;
        }

        return element.attr("href")
                .matches("/b/\\d+");
    }

    private boolean isTranslatorLink(Element element) {

        if (!element.is("a")) {
            return false;
        }

        return element.attr("href")
                .matches("/a/\\d+");
    }

    private Long extractId(String uri) {

        return Long.parseLong(
                uri.substring(
                        uri.lastIndexOf("/") + 1
                )
        );
    }

    private static class SeriesBuilder {

        private final Long id;

        private final String name;

        private final List<SeriesBookResponse> books =
                new ArrayList<>();

        public SeriesBuilder(
                Long id,
                String name
        ) {
            this.id = id;
            this.name = name;
        }

        public SeriesResponse build() {

            return new SeriesResponse(
                    id,
                    name,
                    books
            );
        }
    }

}
