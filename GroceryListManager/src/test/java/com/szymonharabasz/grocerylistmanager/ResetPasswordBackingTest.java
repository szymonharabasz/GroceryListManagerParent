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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordBackingTest {

    @Mock
    UserService mockUserService;
    @Mock
    HashingService mockHashingService;
    @Mock
    FacesContext mockFacesContext;
    @Mock
    ExternalContext mockExternalContext;

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
    @DisplayName("Reset password saves user with a new password")
    public void resetPasswordSavesUser() throws IOException {

        when(mockUserService.findByName(userName)).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(userId)).thenReturn(Optional.of(salt));
        when(mockFacesContext.getExternalContext()).thenReturn(mockExternalContext);
        when(mockExternalContext.getRequestContextPath()).thenReturn("contextpath");

        ResetPasswordBacking resetPasswordBacking = new ResetPasswordBacking(mockUserService, mockHashingService, mockFacesContext);
        resetPasswordBacking.setUserName(userName);
        resetPasswordBacking.setToken("token");
        resetPasswordBacking.load();
        String newPassword = "password";
        resetPasswordBacking.setNewPassword(newPassword);
        resetPasswordBacking.resetPassword();

        assertEquals(user.getPasswordHash(), HashingService.createHash(newPassword, salt));
        verify(mockUserService).save(user);
        verify(mockExternalContext).redirect("contextpath/message.xhtml?type=password-changed");
    }

    @Test
    @DisplayName("Shows error if user is not found")
    public void showErrorUserNotFound() throws IOException {

        when(mockUserService.findByName(userName)).thenReturn(Optional.empty());
        when(mockFacesContext.getExternalContext()).thenReturn(mockExternalContext);
        when(mockExternalContext.getRequestContextPath()).thenReturn("contextpath");

        ResetPasswordBacking resetPasswordBacking = new ResetPasswordBacking(mockUserService, mockHashingService, mockFacesContext);
        resetPasswordBacking.setUserName(userName);
        resetPasswordBacking.load();

        verify(mockExternalContext).redirect("contextpath/message.xhtml?type=wrong-token");
    }

    @Test
    @DisplayName("Shows error if salt is not found")
    public void showErrorSaltNotFound() throws IOException {

        when(mockUserService.findByName(userName)).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(userId)).thenReturn(Optional.of(salt));
        when(mockFacesContext.getExternalContext()).thenReturn(mockExternalContext);
        when(mockExternalContext.getRequestContextPath()).thenReturn("contextpath");

        ResetPasswordBacking resetPasswordBacking = new ResetPasswordBacking(mockUserService, mockHashingService, mockFacesContext);
        resetPasswordBacking.setUserName(userName);
        resetPasswordBacking.setToken("wrong");
        resetPasswordBacking.load();

        verify(mockExternalContext).redirect("contextpath/message.xhtml?type=wrong-token");
    }

    @Test
    @DisplayName("Shows error if token hash is wrong")
    public void showErrorWrongTokenHash() throws IOException {

        when(mockUserService.findByName(userName)).thenReturn(Optional.of(user));
        when(mockHashingService.findSaltByUserId(userId)).thenReturn(Optional.empty());
        when(mockFacesContext.getExternalContext()).thenReturn(mockExternalContext);
        when(mockExternalContext.getRequestContextPath()).thenReturn("contextpath");

        ResetPasswordBacking resetPasswordBacking = new ResetPasswordBacking(mockUserService, mockHashingService, mockFacesContext);
        resetPasswordBacking.setUserName(userName);

        resetPasswordBacking.load();

        verify(mockExternalContext).redirect("contextpath/message.xhtml?type=wrong-token");
    }

}
