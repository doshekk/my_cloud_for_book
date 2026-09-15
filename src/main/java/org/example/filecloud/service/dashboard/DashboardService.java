package org.example.filecloud.service.dashboard;

import org.example.filecloud.dao.book.Book;
import org.example.filecloud.dao.book.ReadStatus;
import org.example.filecloud.repository.BookRepository;
import org.example.filecloud.dao.user.User;
import org.example.filecloud.dto.dashbord.DashboardView;
import org.example.filecloud.security.CurrentUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {
    private final CurrentUserService currentUserService;
    private final BookRepository bookRepository;
    
    public DashboardService(CurrentUserService currentUserService, BookRepository bookRepository) {
        this.currentUserService = currentUserService;
        this.bookRepository = bookRepository;
    }

    public DashboardView getInfoAboutUser(){
        User currentUser = currentUserService.getCurrentUser();
        List<Book> books = bookRepository.findByOwner(currentUser);

        int totalSize = books.size();
        int readBooks = 0;
        int readNowBooks = 0;
        int favoriteBooks = 0;

        for (Book book : books){
            if(book.getReadStatus() == ReadStatus.COMPLETED){ readBooks++;}

            if(book.getReadStatus() == ReadStatus.READING){ readNowBooks++;}

            if(book.isFavorite()){ favoriteBooks++;}
        }

        return new DashboardView(books,totalSize,readBooks,readNowBooks,favoriteBooks);
    };

}
