package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import com.szymonharabasz.grocerylistmanager.service.UserTokenWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.enterprise.event.Event;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestPasswordResetBackingTest {
    @Mock
    UserService mockUserService;
    @Mock
    HashingService mockHashingService;
    @Mock
    FacesContext mockFacesContext;
    @Mock
    ExternalContext mockExternalContext;
    @Mock
    Event<UserTokenWrapper> mockEvent;


    String userId = "123";
    String userName = "name";
    User user = new User(userId, userName, "oldPasswordHash", "user@example.com");
    byte[] saltBytes = "foobar".getBytes();
    Salt salt = new Salt(userId, saltBytes);

    @BeforeEach
    void init() {
        user.setPasswordResetTokenHash(new ExpirablePayload(HashingService.createHash("token", salt), Date.from(Instant.now().plus(Duration.ofMinutes(30)))));
    }

    @Test
    @DisplayName("Does not change app state if non-existing e-mail is prrovided")
    void dontChangeStateOnWronngEmail() {
        final String email = "user@example.com";

        when(mockUserService.findByEmail(email)).thenReturn(Optional.empty());
        when(mockFacesContext.getExternalContext()).thenReturn(mockExternalContext);

        RequestPasswordResetBacking requestPasswordResetBacking = new RequestPasswordResetBacking(
                mockFacesContext,
                mockUserService,
                mockHashingService,
                mockEvent
        );
        requestPasswordResetBacking.setEmail(email);
        requestPasswordResetBacking.request();
        verifyNoInteractions(mockHashingService);
        verifyNoInteractions(mockEvent);
        verifyNoMoreInteractions(mockUserService);
    }

    @Test
    @DisplayName("Throws IllegalStateException when good email is provided, but salt was not found")
    void throwOnGoodEmailButBadSalt() {
        when(mockUserService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(user.getId())).thenReturn(Optional.empty());
        when(mockFacesContext.getExternalContext()).thenReturn(mockExternalContext);

        RequestPasswordResetBacking requestPasswordResetBacking = new RequestPasswordResetBacking(
                mockFacesContext,
                mockUserService,
                mockHashingService,
                mockEvent
        );
        requestPasswordResetBacking.setEmail(user.getEmail());
        assertThrows(IllegalStateException.class, requestPasswordResetBacking::request);

    }

}
