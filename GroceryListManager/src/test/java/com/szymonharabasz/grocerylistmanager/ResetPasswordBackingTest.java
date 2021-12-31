package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordBackingTest {

    @Mock
    UserService mockUserService;
    @Mock
    HashingService mockHashingService;
    @Mock
    FacesContext mockFacesContext;

    final String userId = "123";
    final String userName = "name";
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
    @DisplayName("Reset password saves user with a new password")
    public void resetPasswordSavesUser() {

        when(mockUserService.findByName(userName)).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(userId)).thenReturn(Optional.of(salt));

        ResetPasswordBacking resetPasswordBacking = new ResetPasswordBacking(mockUserService, mockHashingService, mockFacesContext);
        resetPasswordBacking.setUserName(userName);
        resetPasswordBacking.setToken("token");
        resetPasswordBacking.load();
        String newPassword = "password";
        resetPasswordBacking.setNewPassword(newPassword);
        resetPasswordBacking.resetPassword();

        assertEquals(user.getPasswordHash(), mockHashingService.createHash(newPassword, salt));
        verify(mockUserService).save(user);
    }

    @Test
    @DisplayName("Does not change state if user is not found")
    public void doesNotChangeStateIfUserNotFound() {

        when(mockUserService.findByName(userName)).thenReturn(Optional.empty());

        ResetPasswordBacking resetPasswordBacking = new ResetPasswordBacking(mockUserService, mockHashingService, mockFacesContext);
        resetPasswordBacking.setUserName(userName);
        resetPasswordBacking.load();

        verifyNoMoreInteractions(mockUserService);
    }

    @Test
    @DisplayName("Does not change state if token hash is wrong")
    public void doesNotChangeStateOnWrongTokenHash() {

        when(mockUserService.findByName(userName)).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(userId)).thenReturn(Optional.of(salt));

        ResetPasswordBacking resetPasswordBacking = new ResetPasswordBacking(mockUserService, mockHashingService, mockFacesContext);
        resetPasswordBacking.setUserName(userName);
        resetPasswordBacking.setToken("wrong");
        resetPasswordBacking.load();

        verifyNoMoreInteractions(mockUserService);

    }

    @Test
    @DisplayName("Throws the IllegalStateException if salt is not found")
    public void throwsIfSaltNotFound() {

        when(mockUserService.findByName(userName)).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(userId)).thenReturn(Optional.empty());

        ResetPasswordBacking resetPasswordBacking = new ResetPasswordBacking(mockUserService, mockHashingService, mockFacesContext);
        resetPasswordBacking.setUserName(userName);

        assertThrows(IllegalStateException.class, resetPasswordBacking::load);
    }

}
