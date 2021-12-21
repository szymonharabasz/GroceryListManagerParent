package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User, String> {
    Optional<User> findByName(String name);
    Optional<User> findByConfirmationToken(String token);
    Optional<User> findByEmail(String email);
    List<User> findAll();
}
