package com.hr.back.service.impl;

import com.hr.back.model.Account;
import com.hr.back.repository.AccountRepository;
import com.hr.back.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public void deleteById(Long id) {
        accountRepository.deleteById(id);
    }

    @Override
    public List<Account> findByHRManager(Long hrManagerId) {
        return accountRepository.findByHrManagerId(hrManagerId);
    }

    @Override
    public List<Account> findByIntermediaryClient(Long clientId) {
        return accountRepository.findByIntermediaryClientId(clientId);
    }
} 