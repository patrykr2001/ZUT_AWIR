package edu.zut.awir.awir9.repository;

import edu.zut.awir.awir9.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByName(String username);

    boolean existsByEmailIgnoreCase(String email);
}