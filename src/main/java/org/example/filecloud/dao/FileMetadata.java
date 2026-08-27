package org.example.filecloud.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "fileMetaData")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String UUID;
    private String name;
    private String fileStoragePath;
}
