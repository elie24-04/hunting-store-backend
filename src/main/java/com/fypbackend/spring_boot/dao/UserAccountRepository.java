package com.fypbackend.spring_boot.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fypbackend.spring_boot.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByEmail(String email);
}

