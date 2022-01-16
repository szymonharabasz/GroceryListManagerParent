package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.domain.UserRepository;
import jakarta.nosql.mapping.document.DocumentTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    UserRepository mockUserRepository;
    @Mock
    RandomService mockRandomService;
    @Mock
    DocumentTemplate mockDocumentTemplate;

    @Test
    public void returnEmptyOptionForNullName() {
        UserService service = new UserService(mockUserRepository, mockDocumentTemplate, mockRandomService);
        assertEquals(Optional.empty(), service.findByName(null));
    }

    @Test
    public void useRepositoryForNotNullName() {
        UserService service = new UserService(mockUserRepository, mockDocumentTemplate, mockRandomService);
        service.findByName("name");
        verify(mockUserRepository).findByName("name");
    }

    @Test
    public void newUserNotConfirmed() {
        UserService service = new UserService(mockUserRepository, mockDocumentTemplate, mockRandomService);
        User user = service.createUser("1", "name", "passwordHash", "user@example.com");
        assertFalse(user.isConfirmed());
    }

    @Test
    public void newUserHasConfirmationToken() {
        when(mockRandomService.getAlphanumeric(32)).thenReturn("token");
        UserService service = new UserService(mockUserRepository, mockDocumentTemplate, mockRandomService);
        User user = service.createUser("1", "name", "passwordHash", "user@example.com");
        assertNotNull(user.getConfirmationToken());
        assertNotNull(user.getConfirmationToken().getPayload());
    }
}


