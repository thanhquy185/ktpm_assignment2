package com.shopcart.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopcart.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE username = 'customer' OR 1 = 1", nativeQuery = true)
    public List<User> findByUsernameError();
}
