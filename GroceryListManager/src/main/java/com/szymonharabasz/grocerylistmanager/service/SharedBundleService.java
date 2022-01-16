package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.SharedBundle;
import com.szymonharabasz.grocerylistmanager.domain.SharedBundleRepository;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Optional;

@RequestScoped
public class SharedBundleService {
    @Inject
    @Database(DatabaseType.DOCUMENT)
    private SharedBundleRepository repository;

    public SharedBundleService(SharedBundleRepository repository) {
        this.repository = repository;
    }

    public SharedBundleService() {
        this(null);
    }

    public Optional<SharedBundle> findById(String id) {
        return repository.findById(id);
    }

    public void save(SharedBundle bundle) {
        repository.save(bundle);
    }

    public void moveUp(SharedBundle bundle, String listId) {
        bundle.moveListIdUp(listId);
        repository.save(bundle);
    }

    public void moveDown(SharedBundle bundle, String listId) {
        bundle.moveListIdDown(listId);
        repository.save(bundle);
    }
}
