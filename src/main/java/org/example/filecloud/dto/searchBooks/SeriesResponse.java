package org.example.filecloud.dto.searchBooks;

import java.util.List;

public record SeriesResponse(
        Long id,
        String name,
        List<SeriesBookResponse> books
) {
}