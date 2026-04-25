package edu.zut.awir.awir6.service;

import edu.zut.awir.awir6.model.User;
import edu.zut.awir.awir6.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional
    public User save(User user) {
        if (emailAlreadyTakenByAnotherUser(user)) {
            throw new DuplicateEmailException(user.getEmail());
        }

        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            // Zabezpieczenie na wyścig zapisów równoległych.
            throw new DuplicateEmailException(user.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private boolean emailAlreadyTakenByAnotherUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return false;
        }

        if (user.getId() == null) {
            return repository.existsByEmailIgnoreCase(user.getEmail());
        }

        var current = repository.findById(user.getId()).orElse(null);
        if (current != null && current.getEmail() != null && current.getEmail().equalsIgnoreCase(user.getEmail())) {
            return false;
        }

        return repository.existsByEmailIgnoreCase(user.getEmail());
    }
}
