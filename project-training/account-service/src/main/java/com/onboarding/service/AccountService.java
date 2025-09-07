package com.onboarding.service;

import com.onboarding.model.Account;
import com.onboarding.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createInactiveAccount(Map<String, Object> creationData) {
        Long customerId = Long.parseLong(creationData.get("customerId").toString());
        Long kycApplicationId = Long.parseLong(creationData.get("kycApplicationId").toString());
        
        LOGGER.info("Attempting to create inactive account for customer ID: {}", customerId);

        accountRepository.findByCustomerId(customerId).ifPresent(acc -> {
            throw new RuntimeException("Customer already has an account.");
        });

        Account account = new Account();
        
        // --- Core Details ---
        account.setCustomerId(customerId);
        account.setKycApplicationId(kycApplicationId);
        account.setAccountType((String) creationData.get("accountType"));
        account.setAccountStatus("INACTIVE");
        
        // *** THE FIX IS HERE: Generate and set the account number before saving ***
        account.setAccountNumber(generateAccountNumber());
        
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());
        
        // --- Bank-Specific Details ---
        account.setBranchName("HDFC Bank, Vashi Branch");
        account.setIfscCode("HDFC0000123");
        account.setMicrCode("400240123");
        account.setModeOfOperation("Single");

        // --- Details from Customer Registration ---
        account.setNomineeRegistered((Boolean) creationData.getOrDefault("nomineeRegistered", false));
        account.setNomineeName((String) creationData.get("nomineeName"));
        account.setNetBankingEnabled((Boolean) creationData.getOrDefault("netBankingEnabled", false));
        account.setDebitCardIssued((Boolean) creationData.getOrDefault("debitCardIssued", false));
        account.setChequeBookIssued((Boolean) creationData.getOrDefault("chequeBookIssued", false));

        Account savedAccount = accountRepository.save(account);
        LOGGER.info("Successfully created INACTIVE account {} for customer ID: {}", savedAccount.getAccountNumber(), customerId);
        return savedAccount;
    }
    
    @Transactional
    public Account activateAccount(Long customerId) {
        LOGGER.info("Attempting to activate account for customer ID: {}", customerId);
        Account account = accountRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new RuntimeException("Account not found for customer ID: " + customerId));
        
        account.setAccountStatus("ACTIVE");
        account.setDateOfAccountOpening(LocalDateTime.now());

        if (Boolean.TRUE.equals(account.getDebitCardIssued())) {
            account.setDebitCardLast4Digits(generateLastFourDigits());
        }

        if (Boolean.TRUE.equals(account.getChequeBookIssued())) {
            account.setChequeBookLeaves(25);
        }

        Account updatedAccount = accountRepository.save(account);
        LOGGER.info("Successfully ACTIVATED account {} for customer ID: {}", updatedAccount.getAccountNumber(), customerId);
        return updatedAccount;
    }

    private String generateAccountNumber() {
        long number = ThreadLocalRandom.current().nextLong(100_000_000_000L, 1_000_000_000_000L);
        return String.valueOf(number);
    }
    
    private String generateLastFourDigits() {
        return String.format("%04d", new Random().nextInt(10000));
    }
    
    @Transactional
    public Account updateAccountDetails(Long customerId, Map<String, Object> updateData) {
        LOGGER.info("Attempting to update details for account linked to customer ID: {}", customerId);
        Account account = accountRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new RuntimeException("Account not found for customer ID: " + customerId));

        // Selectively update fields if they are present in the update map
        if (updateData.containsKey("nomineeRegistered")) {
            account.setNomineeRegistered((Boolean) updateData.get("nomineeRegistered"));
        }
        if (updateData.containsKey("nomineeName")) {
            account.setNomineeName((String) updateData.get("nomineeName"));
        }
        // Add more fields here if needed in the future (e.g., net banking status)

        return accountRepository.save(account);
    }
    
    @Transactional
    public Account deactivateAccount(Long customerId) {
        LOGGER.info("Attempting to deactivate account for customer ID: {}", customerId);
        Account account = accountRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new RuntimeException("Account not found for customer ID: " + customerId));
            
        if ("INACTIVE".equals(account.getAccountStatus())) {
            LOGGER.warn("Account for customer ID {} is already inactive.", customerId);
            return account; // No change needed, just return the current state
        }
        
        account.setAccountStatus("INACTIVE");
        
        Account updatedAccount = accountRepository.save(account);
        LOGGER.info("Successfully DEACTIVATED account {} for customer ID: {}", updatedAccount.getAccountNumber(), customerId);
        return updatedAccount;
    }
    
}