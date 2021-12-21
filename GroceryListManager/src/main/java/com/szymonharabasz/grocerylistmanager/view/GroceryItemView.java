package com.szymonharabasz.grocerylistmanager.view;

import com.szymonharabasz.grocerylistmanager.domain.GroceryItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import static org.apache.commons.lang3.ObjectUtils.compare;

public class GroceryItemView implements Serializable {
    private String id;
    private boolean done;
    private String name;
    private String unit;
    private BigDecimal quantity;
    private boolean edited;

    public GroceryItemView(String id, boolean done, String name, String unit, BigDecimal quantity) {
        this.id = id;
        this.done = done;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.edited = false;
    }

    public GroceryItemView(GroceryItem item) {
        this(item.getId(), item.isDone(), item.getName(), item.getUnit(), item.getQuantity());
    }

    public GroceryItem toGroceryItem() {
        return new GroceryItem(id, done, name, unit, quantity);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroceryItemView that = (GroceryItemView) o;
        return compare(that.quantity, quantity) == 0 && name.equals(that.name) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, unit, quantity);
    }

    @Override
    public String toString() {
        return "GroceryItem{" +
                "name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
