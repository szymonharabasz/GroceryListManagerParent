package com.szymonharabasz.grocerylistmanager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UtilsTest {
    @Test
    @DisplayName("Subsequently generated IDs are not the same")
    void idsNotRepeat() {
        assertNotEquals(Utils.generateID(), Utils.generateID());
    }
}
