package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.domain.UserRepository;
import jakarta.nosql.document.DocumentQuery;
import jakarta.nosql.mapping.Database;
import jakarta.nosql.mapping.DatabaseType;
import jakarta.nosql.mapping.document.DocumentTemplate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static jakarta.nosql.document.DocumentQuery.select;

@Named
@ApplicationScoped
public class UserService {

    @Database(DatabaseType.DOCUMENT)
    private final UserRepository repository;
    private final DocumentTemplate documentTemplate;
    private final RandomService randomService;

    @Inject
    public UserService(UserRepository repository, DocumentTemplate documentTemplate, RandomService randomService) {
        this.repository = repository;
        this.documentTemplate = documentTemplate;
        this.randomService = randomService;
    }

    public UserService() {
        this(null, null, null);
    }

    public void save(User user) {
        repository.save(user);
    }

    public Optional<User> findByName(String name) {
        if (name == null) return Optional.empty();
        return repository.findByName(name);
    }

    public Optional<User> findByConfirmationToken(String token) {
        return findBy("confirmationToken._id", token);
    }

    public Optional<User> findByEmail(String email) { return repository.findByEmail(email); }

    public Optional<User> findBy(String field, String value) {
        DocumentQuery query = select().from("User").where(field).eq(value).build();
        return documentTemplate.singleResult(query);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User createUser(String userId, String username, String passwordHash, String email) {
        User user =  new User(userId, username, passwordHash, email);
        Date expiresAt = Date.from(Instant.now().plus(Duration.ofDays(2)));
        user.setConfirmationToken(new ExpirablePayload(randomService.getAlphanumeric(32), expiresAt));
        return user;
    }

    public void moveUp(User user, String listId) {
        System.err.println("Before moving up: " + user);
        user.moveListIdUp(listId);
        System.err.println("After moving up: " + user);
        repository.save(user);
    }

    public void moveDown(User user, String listId) {
        user.moveListIdDown(listId);
        repository.save(user);
    }
}
