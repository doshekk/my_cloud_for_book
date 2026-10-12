package org.example.filecloud.dto;

import org.example.filecloud.dao.book.BookGenre;
import org.example.filecloud.dao.book.ReadStatus;


import java.util.List;

public record SearchedBookSave(
        Long id ,
        String title,
        String description,
        List<String> authors,
        ReadStatus readStatus,
        boolean isFavorite,
        List<BookGenre> genres
) {}
