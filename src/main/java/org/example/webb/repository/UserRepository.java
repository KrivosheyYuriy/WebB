package org.example.webb.repository;

import org.example.webb.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    long count();
}
