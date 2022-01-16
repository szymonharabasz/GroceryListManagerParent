package com.szymonharabasz.grocerylistmanager.service;

import org.apache.commons.lang3.RandomStringUtils;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class RandomService {
    public String getAlphanumeric(int length) {
        return RandomStringUtils.randomAlphanumeric(32);
    }
}
