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
import javax.security.enterprise.SecurityContext;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestNewConfirmationBackingTest {
    @Mock
    RandomService randomService;
    @Mock
    UserService mockUserService;
    @Mock
    HashingService mockHashingService;
    @Mock
    FacesContext mockFacesContext;
    @Mock
    SecurityContext mockSecurityContext;
    @Mock
    Event<User> mockEvent;

    final String userId = "123";
    final String userName = "name";
    final String token = "token";
    final String tokenHash = "tokenHash";
    final byte[] saltBytes = "foobar".getBytes();
    User user;
    Salt salt;
    Principal principal;

    @BeforeEach
    void init() {
        this.user = new User(userId, userName, "oldPasswordHash", "user@example.com");
        this.salt = new Salt(userId, saltBytes);
        user.setPasswordResetTokenHash(new ExpirablePayload(mockHashingService.createHash("token", salt), Date.from(Instant.now().plus(Duration.ofMinutes(30)))));
        principal = () -> user.getName();
    }

    @Test
    @DisplayName("Does not change app state if user is not logged in")
    void dontChangeStateOnWrongEmail() {
        when(mockSecurityContext.getCallerPrincipal()).thenReturn(principal);
        when(mockUserService.findByName(principal.getName())).thenReturn(Optional.empty());

        RequestNewConfirmationBacking requestNewConfirmationBacking = new RequestNewConfirmationBacking(
                randomService,
                mockUserService,
                mockSecurityContext,
                mockEvent
        );
        requestNewConfirmationBacking.request();
        verifyNoInteractions(mockEvent);
        verifyNoMoreInteractions(mockUserService);
    }

    @Test
    @DisplayName("Saves correct information about new confirmation e-mail if all is correct")
    void saveNewConfirmationInformationIfAllCorrect() {
        when(mockSecurityContext.getCallerPrincipal()).thenReturn(principal);
        when(mockUserService.findByName(principal.getName())).thenReturn(Optional.of(user));
        when(randomService.getAlphanumeric(32)).thenReturn(token);

        RequestNewConfirmationBacking requestNewConfirmationBacking = new RequestNewConfirmationBacking(
                randomService,
                mockUserService,
                mockSecurityContext,
                mockEvent
        );
        requestNewConfirmationBacking.request();

        verify(mockUserService).save(user);
        verify(mockEvent).fireAsync(user);
        assertEquals(user.getConfirmationToken().getPayload(), token);

    }

}
