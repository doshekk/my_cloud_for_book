package org.example.filecloud.dto.searchBooks;

public record BookResponse(
        String title,
        Long bookId,
        String author,
        Long authorId
) {
}


