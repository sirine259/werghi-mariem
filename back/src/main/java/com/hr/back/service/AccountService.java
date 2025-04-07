package com.hr.back.service;

import com.hr.back.model.Account;

import java.util.List;

public interface AccountService {
    List<Account> findAll();
    Account findById(Long id);
    Account save(Account account);
    void deleteById(Long id);
    List<Account> findByHRManager(Long hrManagerId);
    List<Account> findByIntermediaryClient(Long clientId);
} 