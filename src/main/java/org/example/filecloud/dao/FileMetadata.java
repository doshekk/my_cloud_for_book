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

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    public String getFileStoragePath() {
        return fileStoragePath;
    }

    public void setFileStoragePath(String fileStoragePath) {
        this.fileStoragePath = fileStoragePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public FileType getFleType() {
        return fileType;
    }

    public void setFleType(FileType fleType) {
        this.fileType = fleType;
    }
}
