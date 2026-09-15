package org.example.filecloud.repository;

import org.example.filecloud.dao.FileMetadata;
import org.example.filecloud.dao.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}
