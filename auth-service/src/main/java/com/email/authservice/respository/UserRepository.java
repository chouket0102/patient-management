package com.email.authservice.respository;


import java.util.Optional;
import java.util.UUID;
import com.email.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}