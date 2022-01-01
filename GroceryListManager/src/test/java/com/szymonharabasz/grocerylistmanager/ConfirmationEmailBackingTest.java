package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.faces.context.FacesContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfirmationEmailBackingTest {
    @Mock
    UserService mockUserService;
    @Mock
    FacesContext mockFacesContext;

    private User user;
    private final String userId = "123";
    private final String userName = "name";
    private final String passwordHash = "passwordHash";
    private final String email = "user@example.com";
    private final String token = "token";
    private final String tokenHash = "tokenHash";

    @BeforeEach
    void init() {
        this.user = new User(userId, userName, passwordHash, email);
    }

    @Test
    @DisplayName("User is confirmed if all works correctly")
    void userConfirmedIfAllCorrect() {
        when(mockUserService.findByConfirmationToken(token)).thenReturn(Optional.of(user));

        ConfirmationEmailBacking confirmationEmailBacking = new ConfirmationEmailBacking(mockUserService, mockFacesContext);
        confirmationEmailBacking.setToken(token);
        confirmationEmailBacking.confirmEmail();

        assertTrue(user.isConfirmed());
        assertNull(user.getConfirmationToken());
        verify(mockUserService).save(user);
    }
}
