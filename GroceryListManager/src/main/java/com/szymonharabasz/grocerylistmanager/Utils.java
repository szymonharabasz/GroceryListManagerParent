package com.szymonharabasz.grocerylistmanager;

import java.util.UUID;

public class Utils {
    public static String generateID() {
        return UUID.randomUUID().toString();
    }

}
