package org.example.filecloud.service;

import org.example.filecloud.security.CurrentUserService;
import org.example.filecloud.security.RestClientConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class SearchBookService {
    BooksService booksService;
    CurrentUserService currentUserService;
    RestClient restClient;

    public SearchBookService(BooksService booksService, CurrentUserService currentUserService, RestClientConfig clientConfig) {
        this.booksService = booksService;
        this.currentUserService = currentUserService;
        this.restClient = clientConfig.BooksRestClient();
    }

    public String searchBooks(String query, int page) {
        URI uri = UriComponentsBuilder
                .fromPath("/booksearch")
                .queryParam("ask",query+"&chb=on")
                .build()
                .toUri();
        System.out.println(uri);

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
    }

    public String searchAuthors(String query, int page) {
        URI uri = UriComponentsBuilder
                .fromPath("/booksearch")
                .queryParam("ask", query + "&cha=on")
                .build()
                .toUri();
        System.out.println(uri);

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
    }

    public String seriesBooks(String query, int page) {
        URI uri = UriComponentsBuilder
                .fromPath("/booksearch")
                .queryParam("ask", query + "&chs=on")
                .build()
                .toUri();
        System.out.println(uri);

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
    }

    public String book(Long id, int page){
        URI uri = UriComponentsBuilder
                .fromPath("/b/" + id)
                .build()
                .toUri();
        System.out.println(uri);
        String html = restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
        System.out.println(html);
        return html;
    }

    public String author(Long id){
        URI uri = UriComponentsBuilder
                .fromPath("/a/" + id)
                .build()
                .toUri();
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
    }

    public String series(Long id){
        URI uri = UriComponentsBuilder
                .fromPath("/sequence/" + id)
                .build()
                .toUri();
        System.out.println(uri);
        String html = restClient.get()
                .uri(uri)
                .retrieve()
                .body(String.class);
        return html;
    }
}
