package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.domain.UserRepository;
import jakarta.nosql.mapping.document.DocumentTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository mockUserRepository;
    @Mock
    RandomService mockRandomService;
    @Mock
    DocumentTemplate mockDocumentTemplate;

    @Test
    @DisplayName("Returns Option.empty() for null name")
    void returnEmptyForNullName() {
        UserService service = new UserService(mockUserRepository, mockDocumentTemplate, mockRandomService);
        assertEquals(Optional.empty(), service.findByName(null));
    }

    @Test
    @DisplayName("Uses repository for not null name")
    void useRepositoryForNotNullName() {
        UserService service = new UserService(mockUserRepository, mockDocumentTemplate, mockRandomService);
        service.findByName("name");
        verify(mockUserRepository).findByName("name");
    }

    @Test
    @DisplayName("Newly created user is not confirmed")
    void newUserNotConfirmed() {
        UserService service = new UserService(mockUserRepository, mockDocumentTemplate, mockRandomService);
        User user = service.createUser("1", "name", "passwordHash", "user@example.com");
        assertFalse(user.isConfirmed());
    }

    @Test
    @DisplayName("Newly created user has a confirmation token")
    void newUserHasConfirmationToken() {
        when(mockRandomService.getAlphanumeric(32)).thenReturn("token");
        UserService service = new UserService(mockUserRepository, mockDocumentTemplate, mockRandomService);
        User user = service.createUser("1", "name", "passwordHash", "user@example.com");
        assertNotNull(user.getConfirmationToken());
        assertNotNull(user.getConfirmationToken().getPayload());
    }
}


