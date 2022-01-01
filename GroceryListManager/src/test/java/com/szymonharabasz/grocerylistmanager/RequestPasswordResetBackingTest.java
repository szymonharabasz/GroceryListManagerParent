package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.RandomService;
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
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestPasswordResetBackingTest {
    @Mock
    RandomService mockRandomService;
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

    final String userId = "123";
    final String userName = "name";
    final String token = "token";
    final String tokenHash = "tokenHash";
    final byte[] saltBytes = "foobar".getBytes();
    User user;
    Salt salt;

    @BeforeEach
    void init() {
        this.user = new User(userId, userName, "oldPasswordHash", "user@example.com");
        this.salt = new Salt(userId, saltBytes);
        user.setPasswordResetTokenHash(new ExpirablePayload(mockHashingService.createHash("token", salt), Date.from(Instant.now().plus(Duration.ofMinutes(30)))));
    }

    @Test
    @DisplayName("Does not change app state if non-existing e-mail is prrovided")
    void dontChangeStateOnWrongEmail() {
        when(mockUserService.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        RequestPasswordResetBacking requestPasswordResetBacking = new RequestPasswordResetBacking(
                mockRandomService,
                mockUserService,
                mockHashingService,
                mockEvent
        );
        requestPasswordResetBacking.setEmail(user.getEmail());
        requestPasswordResetBacking.request();
        verifyNoInteractions(mockEvent);
        verifyNoMoreInteractions(mockUserService);
    }

    @Test
    @DisplayName("Throws IllegalStateException when good email is provided, but salt was not found")
    void throwOnGoodEmailButBadSalt() {
        when(mockUserService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(user.getId())).thenReturn(Optional.empty());

        RequestPasswordResetBacking requestPasswordResetBacking = new RequestPasswordResetBacking(
                mockRandomService,
                mockUserService,
                mockHashingService,
                mockEvent
        );
        requestPasswordResetBacking.setEmail(user.getEmail());
        assertThrows(IllegalStateException.class, requestPasswordResetBacking::request);

    }

    @Test
    @DisplayName("Saves correct information about requested password change if all is correct")
    void savePasswordResetInformationIfAllCorrect() {
        when(mockUserService.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(user.getId())).thenReturn(Optional.of(salt));
        when(mockHashingService.createHash(token, salt)).thenReturn(tokenHash);
        when(mockRandomService.getAlphanumeric(32)).thenReturn(token);

        RequestPasswordResetBacking requestPasswordResetBacking = new RequestPasswordResetBacking(
                mockRandomService,
                mockUserService,
                mockHashingService,
                mockEvent
        );
        requestPasswordResetBacking.setEmail(user.getEmail());
        requestPasswordResetBacking.request();

        verify(mockUserService).save(user);
        verify(mockEvent).fireAsync(new UserTokenWrapper(user, token));
        assertEquals(user.getPasswordResetTokenHash().getPayload(), tokenHash);

    }

}
