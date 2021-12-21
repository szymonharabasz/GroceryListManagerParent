package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Entity("GroceryList")
public class GroceryList implements Serializable {
    @Id
    private String id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private List<GroceryItem> items = new ArrayList<>();

    private Logger logger = Logger.getLogger(GroceryList.class.getName());

    public GroceryList() {}

    public GroceryList(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroceryList that = (GroceryList) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        logger.severe("Nmme of the list changes to " + name);
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GroceryItem> getItems() { return items; }

    public void setItems(List<GroceryItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "GroceryList{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public void addItem(GroceryItem item) {
        items.add(item);
    }

    public List<GroceryItem> findAll() {
        return items;
    }
}
