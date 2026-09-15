package org.example.filecloud.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.filecloud.dao.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(@NotBlank(message = "Вкажіть імя") @Size(min = 1, max = 50, message = "імя має містити до 50 символів") String username);
    User findByUsername(@NotBlank(message = "Вкажіть імя") @Size(min = 1, max = 50, message = "імя має містити до 50 символів") String username);
}
