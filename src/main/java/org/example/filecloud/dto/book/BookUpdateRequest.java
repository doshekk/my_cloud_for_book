package org.example.filecloud.dto.book;

import org.example.filecloud.dao.book.BookGenre;
import org.example.filecloud.dao.book.ReadStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record BookUpdateRequest(
        String title,
        String description,
        boolean favorite,
        ReadStatus readStatus,
        List<String> author,
        List<BookGenre> bookGenre,
        MultipartFile file,
        MultipartFile cover
) {
}
