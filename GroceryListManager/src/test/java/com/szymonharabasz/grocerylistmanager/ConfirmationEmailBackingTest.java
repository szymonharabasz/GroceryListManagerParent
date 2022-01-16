package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.faces.context.FacesContext;
import java.util.Optional;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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

    @Before
    public void init() {
        this.user = new User(userId, userName, passwordHash, email);
    }

    @Test
    public void userConfirmedIfAllCorrect() {
        when(mockUserService.findByConfirmationToken(token)).thenReturn(Optional.of(user));

        ConfirmationEmailBacking confirmationEmailBacking = new ConfirmationEmailBacking(mockUserService, mockFacesContext);
        confirmationEmailBacking.setToken(token);
        confirmationEmailBacking.confirmEmail();

        assertTrue(user.isConfirmed());
        assertNull(user.getConfirmationToken());
        verify(mockUserService).save(user);
    }
}
