package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

@Entity("Salt")
public class Salt {
    @Id
    private String userId;
    @Column
    byte[] salt;

    public Salt() {}

    public Salt(String id, byte[] salt) {
        this.userId = id;
        this.salt = salt;
    }

    public String getUserId() {
        return userId;
    }

    public byte[] getSalt() {
        return salt;
    }
}
