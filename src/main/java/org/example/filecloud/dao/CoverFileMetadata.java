package org.example.filecloud.dao;

import jakarta.persistence.*;

@Entity
@Table(name = "coverMetadata")
public class CoverFileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String UUID;
    private String fileStoragePath;

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getFileStoragePath() {
        return fileStoragePath;
    }

    public void setFileStoragePath(String fileStoragePath) {
        this.fileStoragePath = fileStoragePath;
    }
}
