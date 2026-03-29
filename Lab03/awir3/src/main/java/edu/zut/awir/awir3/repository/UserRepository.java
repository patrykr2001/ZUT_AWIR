package edu.zut.awir.awir3.repository;

import edu.zut.awir.awir3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByName(String username);
}