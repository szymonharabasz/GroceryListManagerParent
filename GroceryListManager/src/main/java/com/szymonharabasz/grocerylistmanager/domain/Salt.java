package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

import java.util.Arrays;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salt salt1 = (Salt) o;
        return Objects.equals(userId, salt1.userId) && Arrays.equals(salt, salt1.salt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(userId);
        result = 31 * result + Arrays.hashCode(salt);
        return result;
    }
}
