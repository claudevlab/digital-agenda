package com.claudev.agenda.repository;

import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findById (Long id);

    Optional<User> findByEmail (String email);

    Optional<User> findByUsername (String username);

    List<User> findByRole (Role role);

}
