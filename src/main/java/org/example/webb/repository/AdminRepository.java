package org.example.webb.repository;

import org.example.webb.entity.Admin;

public interface AdminRepository extends CrudRepository<Admin, Long> {
    Admin findByUsername(String username);
}
