package com.hr.back.repository;

import com.hr.back.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByHrManagerId(Long hrManagerId);
    List<Account> findByIntermediaryClientId(Long intermediaryClientId);
} 