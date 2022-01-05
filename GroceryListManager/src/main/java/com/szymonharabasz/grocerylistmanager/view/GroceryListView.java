package com.szymonharabasz.grocerylistmanager.view;

import com.szymonharabasz.grocerylistmanager.domain.GroceryList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GroceryListView implements Serializable {
    private String id;
    private String name;
    private String description;
    private Date lastModified;
    private List<GroceryItemView> items = new ArrayList<>();
    private boolean edited;
    private boolean expanded;

    private Logger logger = Logger.getLogger(GroceryListView.class.getName());

    public GroceryListView(String id, String name, String description, Date lastModified) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.lastModified = lastModified;
        this.edited = false;
        this.expanded = true;
    }

    public GroceryListView(String id) {
        this(id, "", "", new Date());
    }

    public GroceryListView(GroceryList list) {
        this(list.getId(), list.getName(), list.getDescription(), list.getLastModified());
        this.items = list.getItems().stream().map(GroceryItemView::new).collect(Collectors.toList());
    }

    public GroceryList toGroceryList() {
        GroceryList groceryList = new GroceryList(id, name, description, lastModified);
        groceryList.setItems(items.stream()
                .map(GroceryItemView::toGroceryItem).collect(Collectors.toList()));
        return groceryList;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroceryListView listView = (GroceryListView) o;
        return Objects.equals(id, listView.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public List<GroceryItemView> getItems() { return items; }

    public void setItems(List<GroceryItemView> items) {
        this.items = items;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public boolean isEdited() {
        return edited;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public String toString() {
        return "GroceryListView{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", lastModified=" + lastModified +
                ", edited=" + edited +
                ", expanded=" + expanded +
                '}';
    }

    public void addItem(GroceryItemView item) {
        items.add(item);
    }

    public List<GroceryItemView> findAll() {
        return items;
    }
}
