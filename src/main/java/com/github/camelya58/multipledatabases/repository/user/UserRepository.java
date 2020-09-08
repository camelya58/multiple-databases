package com.github.camelya58.multipledatabases.repository.user;

import com.github.camelya58.multipledatabases.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface UserRepository sets connection with H2 database "testdb".
 *
 * @author Kamila Meshcheryakova
 * created 08.09.2020
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByAgeGreaterThanEqual(int age);
}
