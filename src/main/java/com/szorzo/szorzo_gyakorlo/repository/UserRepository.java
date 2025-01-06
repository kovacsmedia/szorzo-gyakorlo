package com.szorzo.szorzo_gyakorlo.repository;

import com.szorzo.szorzo_gyakorlo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username); // Ez a metódus lett hozzáadva
}
