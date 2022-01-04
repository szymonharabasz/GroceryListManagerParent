package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.SharedBundle;
import com.szymonharabasz.grocerylistmanager.domain.SharedBundleRepository;
import jakarta.nosql.mapping.Database;
import jakarta.nosql.mapping.DatabaseType;

import java.util.Optional;

public class SharedBundleService {
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
}
