package com.szymonharabasz.grocerylistmanager.domain;

import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Objects;

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
       // System.err.println("Type of listIds: " + listIds.getClass().getCanonicalName());
       // listIds.add(id);
       // Old version of jnosql requires makes the list immutable, so we use immutable techniques
        listIds = Stream.concat(
            listIds.stream(), Arrays.asList(id).stream()
        ).collect(Collectors.toList());
    }

    public void removeListId(String id) {
       // listIds.remove(id);
        System.err.println("ID TO DELETE " + id);
        System.err.println("BEFORE DELETING " + listIds);
        listIds = listIds.stream().filter(listId -> !Objects.equal(listId, id))
            .collect(Collectors.toList());
        System.err.println("AFTER DELETING " + listIds);
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
            Stream<String> a = listIds.subList(0, index-1).stream();
            Stream<String> b = Arrays.asList(listIds.get(index), listIds.get(index-1)).stream();
            Stream<String> c = listIds.subList(index+1, listIds.size()).stream();
            listIds = Stream.of(a, b, c).flatMap(s -> s).collect(Collectors.toList());
           // Collections.swap(listIds, index, index-1);
            System.err.println("After swapping " + listIds);
        }
    }

    public void moveListIdDown(String listId) {
        int index = listIds.indexOf(listId);
        if (index < listIds.size()-1) {
           // Collections.swap(listIds, index, index+1);
            Stream<String> a = listIds.subList(0, index).stream();
            Stream<String> b = Arrays.asList(listIds.get(index+1), listIds.get(index)).stream();
            Stream<String> c = listIds.subList(index+2, listIds.size()).stream();
            listIds = Stream.of(a, b, c).flatMap(s -> s).collect(Collectors.toList());
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
