package com.szymonharabasz.grocerylistmanager.domain;

import javax.enterprise.context.ApplicationScoped;

import org.jnosql.artemis.Repository;

@ApplicationScoped
public interface SaltRepository extends Repository<Salt, String> {}
