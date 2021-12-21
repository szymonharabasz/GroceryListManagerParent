package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.domain.UserRepository;
import jakarta.nosql.document.DocumentQuery;
import jakarta.nosql.mapping.Database;
import jakarta.nosql.mapping.DatabaseType;
import jakarta.nosql.mapping.document.DocumentTemplate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

import static jakarta.nosql.document.DocumentQuery.select;

@Named
@ApplicationScoped
public class UserService {

    @Inject
    @Database(DatabaseType.DOCUMENT)
    UserRepository repository;

    @Inject
    private DocumentTemplate documentTemplate;

    public void save(User user) {
        repository.save(user);
    }

    public Optional<User> findByName(String name) {
        if (name == null) return Optional.empty();
        return repository.findByName(name);
    }

    public Optional<User> findByConfirmationToken(String token) { return repository.findByConfirmationToken(token); }

    public Optional<User> findByEmail(String email) { return repository.findByEmail(email); }

    public Optional<User> findBy(String field, String value) {
        DocumentQuery query = select().from("User").where(field).eq(value).build();
        return documentTemplate.singleResult(query);
    }

    public List<User> findAll() {
        return repository.findAll();
    }
}
