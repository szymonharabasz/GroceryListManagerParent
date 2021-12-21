package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Repository;

import java.util.List;

public interface ListsRepository extends Repository<GroceryList, String> {
    List<GroceryList> findAll();
}
