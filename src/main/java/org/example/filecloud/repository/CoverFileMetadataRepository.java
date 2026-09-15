package org.example.filecloud.repository;

import org.example.filecloud.dao.CoverFileMetadata;
import org.example.filecloud.dao.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoverFileMetadataRepository extends JpaRepository<CoverFileMetadata, Long> {
}
