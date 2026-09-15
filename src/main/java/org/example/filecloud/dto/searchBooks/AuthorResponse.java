package org.example.filecloud.dto.searchBooks;

import java.util.List;

public record AuthorResponse (
        String name,
        String description,
        List<SeriesResponse> seriesList,
        String imageUri
){
}
