package edu.zut.awir.awir10.service;

import edu.zut.awir.awir10.model.User;
import edu.zut.awir.awir10.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository repository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private UserService userService;

    @Test
    void saveForCreateSendsCreatedNotification() {
        User payload = new User(null, "Jan Kowalski", "jan@example.com");
        User saved = new User(1L, "Jan Kowalski", "jan@example.com");

        when(repository.existsByEmailIgnoreCase(payload.getEmail())).thenReturn(false);
        when(repository.save(payload)).thenReturn(saved);

        userService.save(payload);

        verify(mailService).sendUserCreated(saved);
        verify(mailService, never()).sendUserUpdated(any());
    }

    @Test
    void saveForUpdateSendsUpdatedNotification() {
        User payload = new User(7L, "Jan Kowalski", "jan@example.com");
        User current = new User(7L, "Jan Kowalski", "jan@example.com");
        User saved = new User(7L, "Jan Kowalski", "jan@example.com");

        when(repository.findById(7L)).thenReturn(Optional.of(current));
        when(repository.existsById(7L)).thenReturn(true);
        when(repository.save(payload)).thenReturn(saved);

        userService.save(payload);

        verify(mailService).sendUserUpdated(saved);
        verify(mailService, never()).sendUserCreated(any());
    }

    @Test
    void saveWithDuplicateEmailThrowsAndDoesNotSendNotification() {
        User payload = new User(null, "Jan Kowalski", "jan@example.com");
        when(repository.existsByEmailIgnoreCase(payload.getEmail())).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.save(payload));

        verify(repository, never()).save(any());
        verify(mailService, never()).sendUserCreated(any());
        verify(mailService, never()).sendUserUpdated(any());
    }
}
