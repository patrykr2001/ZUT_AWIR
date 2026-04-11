package edu.zut.awir.awir4.service;

import edu.zut.awir.awir4.model.User;
import edu.zut.awir.awir4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    @Transactional
    public User save(User user) {
        return repository.save(user);
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
}
