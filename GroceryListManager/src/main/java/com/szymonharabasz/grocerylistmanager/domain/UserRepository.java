package com.szymonharabasz.grocerylistmanager.domain;

import org.jnosql.artemis.Repository;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface UserRepository extends Repository<User, String> {
    Optional<User> findByName(String name);
    Optional<User> findByConfirmationToken(String token);
    Optional<User> findByEmail(String email);
    List<User> findAll();
}
