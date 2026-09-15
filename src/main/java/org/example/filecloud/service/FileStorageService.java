package org.example.filecloud.service;

import org.example.filecloud.dao.CoverFileMetadata;
import org.example.filecloud.dao.FileMetadata;
import org.example.filecloud.dao.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storagePath;

    public FileStorageService(
            @Value("${file.storage.path}") String basePath
    ) {
        this.storagePath = Paths.get(basePath);
    }

    @Transactional
    public void createUserFolder(User user) {

        Objects.requireNonNull(user, "Користувач не може бути null");

        Path userPath = getUserPath(user);

        try {
            Files.createDirectories(userPath);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Не вдалося створити папку користувача: " + userPath,
                    e
            );
        }
    }

    @Transactional
    public FileMetadata saveFileInFolder(
            MultipartFile file,
            String name,
            User user
    ) {

        validateFile(file);
        Objects.requireNonNull(user, "Користувач не може бути null");

        UUID uniqueKey = UUID.randomUUID();

        Path userPath = getUserPath(user);

        try {
            Files.createDirectories(userPath);

            Path filePath = userPath.resolve(uniqueKey.toString());

            file.transferTo(filePath);

            FileMetadata fileMetadata = new FileMetadata();

            fileMetadata.setFileStoragePath(filePath.toString());
            fileMetadata.setName(name);
            fileMetadata.setUUID(uniqueKey.toString());

            return fileMetadata;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Не вдалося зберегти файл",
                    e
            );
        }
    }

    @Transactional
    public CoverFileMetadata saveCoverInFolder(
            MultipartFile file,
            User user
    ) {

        validateFile(file);
        Objects.requireNonNull(user, "Користувач не може бути null");

        UUID uniqueKey = UUID.randomUUID();

        Path userPath = getUserPath(user);

        try {
            Files.createDirectories(userPath);

            Path filePath = userPath.resolve(uniqueKey.toString());

            file.transferTo(filePath);

            CoverFileMetadata coverFileMetadata =
                    new CoverFileMetadata();

            coverFileMetadata.setFileStoragePath(filePath.toString());
            coverFileMetadata.setUUID(uniqueKey.toString());

            return coverFileMetadata;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Не вдалося зберегти обкладинку",
                    e
            );
        }
    }

    public void deleteBookFiles(
            FileMetadata file,
            CoverFileMetadata cover,
            User user
    ) {
        Objects.requireNonNull(user, "Користувач не може бути null");

        Path userPath = getUserPath(user);

        deleteFile(userPath, file.getUUID());

        if (cover != null) {
            deleteFile(userPath, cover.getUUID());
        }
    }

    private void deleteFile(Path directory, String fileName) {
        try {
            Path filePath = directory.resolve(fileName);

            if (Files.deleteIfExists(filePath)) {
                System.out.println("Файл видалено: " + filePath);
            } else {
                System.out.println("Файл не знайдено: " + filePath);
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Не вдалося видалити файл: " + fileName,
                    e
            );
        }
    }

    @Transactional
    private Path getUserPath(User user) {
        return storagePath.resolve(
                String.valueOf(user.getId())
        );
    }

    @Transactional
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("Файл пустий");
        }
    }
}