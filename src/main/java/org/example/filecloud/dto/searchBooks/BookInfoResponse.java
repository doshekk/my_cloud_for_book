package org.example.filecloud.dto.searchBooks;

import java.util.List;

public record BookInfoResponse(
        String title,
        String description,
        List<String> authors,
        List<Long> authorsId,
        String coverUrl
) {
}
