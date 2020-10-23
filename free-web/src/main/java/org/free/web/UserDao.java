package org.free.web;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {
	User findUserByUsername(String username);
}