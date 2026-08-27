package org.example.filecloud.dao;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;
    private List<UserRole> roles;

    @Enumerated(EnumType.STRING)
    private List<BookGenre> bookGenre;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToOne
    private FileMetadata fileMetadata;


}
