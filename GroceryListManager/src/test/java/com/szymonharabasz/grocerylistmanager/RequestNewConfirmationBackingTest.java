package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.RandomService;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.security.enterprise.SecurityContext;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestNewConfirmationBackingTest {
    @Mock
    RandomService mockRandomService;
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

    @Before
    public void init() {
        this.user = new User(userId, userName, "oldPasswordHash", "user@example.com");
        this.salt = new Salt(userId, saltBytes);
        user.setPasswordResetTokenHash(new ExpirablePayload(mockHashingService.createHash("token", salt), Date.from(Instant.now().plus(Duration.ofMinutes(30)))));
        principal = () -> user.getName();
    }

    @Test
    public void dontChangeStateOnWrongEmail() {
        when(mockSecurityContext.getCallerPrincipal()).thenReturn(principal);
        when(mockUserService.findByName(principal.getName())).thenReturn(Optional.empty());

        RequestNewConfirmationBacking requestNewConfirmationBacking = new RequestNewConfirmationBacking(
                mockRandomService,
                mockUserService,
                mockSecurityContext,
                mockEvent
        );
        requestNewConfirmationBacking.request();
        verifyNoInteractions(mockEvent);
        verify(mockUserService, times(0)).save(user);
    }

    @Test
    public void saveNewConfirmationInformationIfAllCorrect() {
        when(mockSecurityContext.getCallerPrincipal()).thenReturn(principal);
        when(mockUserService.findByName(principal.getName())).thenReturn(Optional.of(user));
        when(mockRandomService.getAlphanumeric(32)).thenReturn(token);

        RequestNewConfirmationBacking requestNewConfirmationBacking = new RequestNewConfirmationBacking(
                mockRandomService,
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
