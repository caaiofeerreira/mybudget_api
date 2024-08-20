package com.mybudget.domain.repository;

import com.mybudget.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByEmail(String email);

    @Query("SELECT u FROM tb_user u WHERE u.email = :email")
    Optional<User> existingEmail(@Param("email") String email);
}