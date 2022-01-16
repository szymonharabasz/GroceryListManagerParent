package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.enterprise.event.Event;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegisterBackingTest {
    @Mock
    UserService mockUserService;
    @Mock
    HashingService mockHashingService;
    @Mock
    FacesContext mockFacesContext;
    @Mock
    ExternalContext mockExternalContext;
    @Mock
    Event<User> mockEvent;

    final String userId = "123";
    final String userName = "name";
    final String password = "password";
    final String passwordHash = "passwordHash";
    final String token = "token";
    final String tokenHash = "tokenHash";
    final String email = "user@example.com";
    final byte[] saltBytes = "foobar".getBytes();
    User user;
    Salt salt;

    @Before
    public void init() {
        this.user = new User(userId, userName, "oldPasswordHash", "user@example.com");
        this.salt = new Salt(userId, saltBytes);
    }

    @Test
    public void registersNewUser() {
        when(mockHashingService.createSalt()).thenReturn(salt);
        when(mockHashingService.createHash(password, salt)).thenReturn(passwordHash);
        when(mockUserService.createUser(salt.getUserId(), userName, passwordHash, email))
                .thenReturn(user);

        RegisterBacking registerBacking = new RegisterBacking(
                mockUserService,
                mockHashingService,
                mockEvent
        );
        registerBacking.setUsername(userName);
        registerBacking.setPassword(password);
        registerBacking.setEmail(email);
        registerBacking.register();

        verify(mockUserService).save(user);
        verify(mockEvent).fireAsync(user);

    }

}
