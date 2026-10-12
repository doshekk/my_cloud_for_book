package org.example.filecloud.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class DownloadBookById {
    private final RestClient restClient;

    public DownloadBookById(RestClient restClient) {
        this.restClient = restClient;
    }

    public byte[] downloadBook(Long id) {

        URI uri = UriComponentsBuilder
                .fromPath("/b/" + id + "/epub")
                .build()
                .encode()
                .toUri();

        System.out.println("========== DOWNLOAD BOOK ==========");
        System.out.println("URL: " + uri);

        return restClient.get()
                .uri(uri)
                .exchange((request, response) -> {

                    System.out.println(
                            "STATUS: " + response.getStatusCode()
                    );

                    System.out.println(
                            "CONTENT TYPE: " +
                                    response.getHeaders().getContentType()
                    );

                    System.out.println(
                            "CONTENT LENGTH: " +
                                    response.getHeaders().getContentLength()
                    );

                    byte[] body = response.getBody().readAllBytes();

                    System.out.println(
                            "BODY SIZE: " + body.length + " bytes"
                    );

                    if (body.length >= 4) {
                        System.out.printf(
                                "FIRST BYTES: %02X %02X %02X %02X%n",
                                body[0] & 0xFF,
                                body[1] & 0xFF,
                                body[2] & 0xFF,
                                body[3] & 0xFF
                        );
                    }

                    System.out.println("===================================");

                    return body;
                });
    }

    public byte[] downloadCover(String uriCover) {

        byte[] file = restClient.get()
                .uri(uriCover)
                .retrieve()
                .body(byte[].class);

        System.out.println("COVER URL: " + uriCover);
        System.out.println("COVER FILE: " +
                (file == null ? "NULL" : file.length + " bytes"));

        return file;
    }
}
