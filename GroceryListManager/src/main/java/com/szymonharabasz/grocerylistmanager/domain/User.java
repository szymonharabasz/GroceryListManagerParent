package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity("User")
public class User implements Serializable {
    @Id
    private String id;
    @Column
    private String name;
    @Column
    private String passwordHash;
    @Column
    private String email;
    @Column
    private List<String> listIds = new ArrayList<>();
    @Column
    private boolean confirmed;
    @Column
    private Date registered;
    @Column
    private ExpirablePayload confirmationToken = null;
    @Column
    private ExpirablePayload passwordResetTokenHash = null;

    public User() {}

    public User(String id, String name, String passwordHash, String email) {
        this.id = id;
        this.name = name;
        this.passwordHash = passwordHash;
        this.email = email;
        this.confirmed = false;
        this.registered = new Date();
    }

    public void addListId(String id) {
        listIds.add(id);
    }

    public void removeListId(String id) {
        listIds.remove(id);
    }

    public boolean hasListId(String id) {
        return listIds.contains(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public ExpirablePayload getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(ExpirablePayload confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public ExpirablePayload getPasswordResetTokenHash() {
        return passwordResetTokenHash;
    }

    public void setPasswordResetTokenHash(ExpirablePayload passwordResetToken) {
        this.passwordResetTokenHash = passwordResetToken;
    }

    public void moveListIdUp(String listId) {
        int index = listIds.indexOf(listId);
        System.err.println("Index of " + listId + " is " + index);
        if (index > 0) {
            System.err.println("Before swapping: " + listIds);
            Collections.swap(listIds, index, index-1);
            System.err.println("After swapping " + listIds);
        }
    }

    public void moveListIdDown(String listId) {
        int index = listIds.indexOf(listId);
        if (index < listIds.size()-1) {
            Collections.swap(listIds, index, index+1);
        }
    }

    public int getIndexOfListId(String listId) {
        return listIds.indexOf(listId);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", listIds=" + listIds +
                '}';
    }
}
