package com.szymonharabasz.grocerylistmanager.domain;

import org.jnosql.artemis.Repository;

import java.util.List;
import java.util.Optional;

public interface ListsRepository extends Repository<GroceryList, String> {
    List<GroceryList> findAll();
    Optional<GroceryList> findByDisplayIndex(long index);
}
