package com.szymonharabasz.grocerylistmanager.domain;

import javax.enterprise.context.ApplicationScoped;

import org.jnosql.artemis.Repository;

@ApplicationScoped
public interface SharedBundleRepository extends Repository<SharedBundle, String> {
}
