package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

import java.util.ArrayList;
import java.util.List;

@Entity("SharedBundle")
public class SharedBundle {
    @Id
    private String id;
    @Column
    private String from;
    @Column
    private List<String> listIds = new ArrayList<>();

    public SharedBundle() {
    }

    public SharedBundle(String id, String from, List<String> listIds) {
        this.id = id;
        this.from = from;
        this.listIds = listIds;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public List<String> getListIds() {
        return listIds;
    }

    public boolean hasListId(String id) {
        return listIds.contains(id);
    }

    public int getIndexOfListId(String listId) {
        return listIds.indexOf(listId);
    }

}
