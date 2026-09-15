package org.example.filecloud.service;

import org.example.filecloud.dao.CoverFileMetadata;
import org.example.filecloud.dao.FileMetadata;
import org.example.filecloud.dao.book.Book;
import org.example.filecloud.dao.user.User;
import org.example.filecloud.dto.book.BookUpdateRequest;
import org.example.filecloud.dto.book.BookUploadRequest;
import org.example.filecloud.repository.BookRepository;
import org.example.filecloud.repository.CoverFileMetadataRepository;
import org.example.filecloud.repository.FileMetadataRepository;
import org.example.filecloud.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
public class BooksService {
    private final CurrentUserService currentUserService;
    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;
    private final FileMetadataRepository fileMetadataRepository;
    private final CoverFileMetadataRepository coverFileMetadataRepository;

    public BooksService(CurrentUserService currentUserService, BookRepository bookRepository, FileStorageService fileStorageService, FileMetadataRepository fileMetadataRepository, CoverFileMetadataRepository coverFileMetadataRepository) {
        this.currentUserService = currentUserService;
        this.bookRepository = bookRepository;
        this.fileStorageService = fileStorageService;
        this.fileMetadataRepository = fileMetadataRepository;
        this.coverFileMetadataRepository = coverFileMetadataRepository;
    }


    @Transactional
    public void addBook(BookUploadRequest bookDTO) {
        User user = currentUserService.getCurrentUser();
        Book book = new Book();

        FileMetadata fileMetadata =
                fileStorageService.saveFileInFolder(bookDTO.file(), bookDTO.title(), user);
        CoverFileMetadata coverFileMetadata =
                fileStorageService.saveCoverInFolder(bookDTO.cover(), user);

        book.setTitle(bookDTO.title());
        book.setDescription(bookDTO.description());
        book.setAuthor(bookDTO.author());
        book.setOwner(user);
        book.setReadStatus(bookDTO.readStatus());
        book.setFavorite(bookDTO.favorite());
        book.setCoverFileMetadata(coverFileMetadata);
        book.setFileMetadata(fileMetadata);
        book.setBookGenre(bookDTO.bookGenre());

        fileMetadataRepository.save(fileMetadata);
        coverFileMetadataRepository.save(coverFileMetadata);
        bookRepository.save(book);
    }

    @Transactional
    public Book getBookById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID книги не може бути null");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Такої книги не існує")
                );

        User currentUser = currentUserService.getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException("Користувач не авторизований");
        }

        if (book.getOwner() == null ||
                book.getOwner().getId() != currentUser.getId()) {

            throw new IllegalStateException(
                    "Ви не маєте доступу до цієї книги"
            );
        }

        return book;
    }

    @Transactional
    public void setFavorite(Long id){
        if (id == null) { throw new IllegalArgumentException("ID книги не може бути null"); }

        Book book = bookRepository.findById(id)
                .orElseThrow(()-> new IllegalStateException("Такої книги не знайдено") );


        if (currentUserService.getCurrentUser().equals(book.getOwner())) {
            throw new IllegalStateException(
                    "Ви не маєте доступу до цієї книги"
            );
        }

        book.setFavorite(!book.isFavorite());
        bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID книги не може бути null");
        }

        User currentUser = currentUserService.getCurrentUser();

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException("Такої книги не знайдено")
                );

        if (!Objects.equals(currentUser.getId(), book.getOwner().getId())) {
            throw new IllegalStateException(
                    "Ви не є власником цієї книги, щоб її видалити"
            );
        }

        fileStorageService.deleteBookFiles(
                book.getFileMetadata(),
                book.getCoverFileMetadata(),
                currentUser
        );

        bookRepository.delete(book);
    }

    @Transactional
    public void editBook(Long id, BookUpdateRequest bookUpdate) {

        if (id == null) {
            throw new IllegalArgumentException("ID книги не може бути null");
        }

        if (bookUpdate == null) {
            throw new IllegalArgumentException("Дані для оновлення книги не можуть бути null");
        }

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException("Такої книги не знайдено")
                );

        User currentUser = currentUserService.getCurrentUser();

        book.setTitle(bookUpdate.title());
        book.setDescription(bookUpdate.description());
        book.setAuthor(bookUpdate.author());
        book.setFavorite(bookUpdate.favorite());
        book.setBookGenre(bookUpdate.bookGenre());


        if (bookUpdate.file() != null && !bookUpdate.file().isEmpty()) {

            FileMetadata oldFileMetadata = book.getFileMetadata();

            FileMetadata newFileMetadata =
                    fileStorageService.saveFileInFolder(
                            bookUpdate.file(),
                            bookUpdate.title(),
                            currentUser
                    );

            book.setFileMetadata(newFileMetadata);

            if (oldFileMetadata != null) {
                fileMetadataRepository.delete(oldFileMetadata);
            }

            fileMetadataRepository.save(newFileMetadata);
        }


        if (bookUpdate.cover() != null && !bookUpdate.cover().isEmpty()) {

            CoverFileMetadata oldCoverFileMetadata =
                    book.getCoverFileMetadata();

            CoverFileMetadata newCoverFileMetadata =
                    fileStorageService.saveCoverInFolder(
                            bookUpdate.cover(),
                            currentUser
                    );

            book.setCoverFileMetadata(newCoverFileMetadata);

            if (oldCoverFileMetadata != null) {
                coverFileMetadataRepository.delete(oldCoverFileMetadata);
            }

            coverFileMetadataRepository.save(newCoverFileMetadata);
        }
        
        bookRepository.save(book);
    }


}
