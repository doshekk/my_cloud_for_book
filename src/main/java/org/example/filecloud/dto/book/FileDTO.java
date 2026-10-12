package org.example.filecloud.dto.book;

public record FileDTO(
        byte[] bookFile,
        byte[] coverFile
) {
}
