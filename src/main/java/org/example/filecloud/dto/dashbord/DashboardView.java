package org.example.filecloud.dto.dashbord;

import org.example.filecloud.dao.book.Book;

import java.util.List;

public record DashboardView (
        List<Book> books,
        int totalBooks,
        int readBooks,
        int readNowBooks,
        int favoriteBooks
) {
}
