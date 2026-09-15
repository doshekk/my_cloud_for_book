package org.example.filecloud.dto.searchBooks;

import java.util.List;

public record SeriesBookResponse(
        Long id,
        String title,
        List<TranslatorResponse> translators
) {
}