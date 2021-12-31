package com.szymonharabasz.grocerylistmanager.domain;

import jakarta.nosql.mapping.Column;
import jakarta.nosql.mapping.Embeddable;
import jakarta.nosql.mapping.Entity;
import jakarta.nosql.mapping.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity("ExpirablePayload")
public class ExpirablePayload implements Serializable {
    @Id
    private String payload;
    @Column
    private Date expiresAt;

    public ExpirablePayload() {}

    public ExpirablePayload(String url, Date expiresAt) {
        this.payload = url;
        this.expiresAt = expiresAt;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        // We want to treat payloads with null expiration date as invalid and so expired
        return expiresAt == null || expiresAt.before(new Date());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpirablePayload that = (ExpirablePayload) o;
        return Objects.equals(payload, that.payload) && Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload, expiresAt);
    }
}
