package org.example.filecloud.dao.book;

import jakarta.persistence.*;
import org.example.filecloud.dao.CoverFileMetadata;
import org.example.filecloud.dao.FileMetadata;
import org.example.filecloud.dao.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String description;
    private boolean favorite;
    private LocalDateTime addTime;
    private String author;

    @ElementCollection(targetClass = BookGenre.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id")
    )
    @Column(name = "genre")
    private List<BookGenre> bookGenre;

    @Enumerated(EnumType.STRING)
    private ReadStatus readStatus;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToOne
    private FileMetadata fileMetadata;

    @OneToOne(cascade = CascadeType.PERSIST)
    private CoverFileMetadata coverFileMetadata;


    @PrePersist
    protected void onAdd(){
        if(readStatus == null){
            readStatus = ReadStatus.READING;
        }
        addTime = LocalDateTime.now();
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public List<BookGenre> getBookGenre() {
        return bookGenre;
    }

    public void setBookGenre(List<BookGenre> bookGenre) {
        this.bookGenre = bookGenre;
    }

    public ReadStatus getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(ReadStatus readStatus) {
        this.readStatus = readStatus;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public void setFileMetadata(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public String getAuthor() {        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public CoverFileMetadata getCoverFileMetadata() {
        return coverFileMetadata;
    }

    public void setCoverFileMetadata(CoverFileMetadata coverFileMetadata) {
        this.coverFileMetadata = coverFileMetadata;
    }
}
