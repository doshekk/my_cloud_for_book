package org.example.filecloud.repository;

import org.example.filecloud.dao.book.Book;
import org.example.filecloud.dao.user.User;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByOwner(User owner);
    Optional<Book> findById(Long id);
}
