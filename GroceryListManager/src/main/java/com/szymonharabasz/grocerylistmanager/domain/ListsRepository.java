package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Repository;

import java.util.List;
import java.util.Optional;

public interface ListsRepository extends Repository<GroceryList, String> {
    List<GroceryList> findAll();
    Optional<GroceryList> findByDisplayIndex(long index);
}
