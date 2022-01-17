package com.szymonharabasz.grocerylistmanager.domain;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.jnosql.artemis.Repository;

@ApplicationScoped
public interface SaltRepository extends Repository<Salt, String> {
    List<Salt> findAll();
    void deleteByUserId(Iterable<String> ids);
}
