package com.szymonharabasz.grocerylistmanager;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class UtilsTest {
    @Test
    public void idsNotRepeat() {
        assertNotEquals(Utils.generateID(), Utils.generateID());
    }
}
