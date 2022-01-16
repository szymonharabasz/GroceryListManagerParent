package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.SharedBundle;
import com.szymonharabasz.grocerylistmanager.domain.SharedBundleRepository;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@Named
@RequestScoped
public class SharedBundleService {
    @Database(DatabaseType.DOCUMENT)
    private SharedBundleRepository repository;

    @Inject
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
