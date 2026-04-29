package com.claudev.agenda.repository;

import com.claudev.agenda.entity.User;
import com.claudev.agenda.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findById (Long id);

    Optional<User> findByEmail (String email);

    Optional<User> findByUsername (String username);

    List<User> findByRole (Role role);

    //Ricerca case-insensitive su nome, cognome o professione
    @Query("SELECT u FROM User u WHERE u.role = :role AND (" +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.jobTitle) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> searchProfessionals (@Param("search") String search, @Param("role") Role role );

    Optional<User> findByResetPasswordToken (String token);

}
