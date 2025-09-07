package com.onboarding.controller;

import com.onboarding.dto.AccountDTO;
import com.onboarding.model.Account;
import com.onboarding.repository.AccountRepository;
import com.onboarding.service.AccountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/internal/accounts")
public class InternalApiController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public InternalApiController(AccountService accountService, AccountRepository accountRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }
    
 // In InternalApiController.java

    private AccountDTO convertToDto(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setCustomerId(account.getCustomerId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType());
        dto.setAccountStatus(account.getAccountStatus());
        dto.setBalance(account.getBalance());
        
        // --- Mapping all the new fields ---
        dto.setBranchName(account.getBranchName());
        dto.setIfscCode(account.getIfscCode());
        dto.setMicrCode(account.getMicrCode());
        dto.setDateOfAccountOpening(account.getDateOfAccountOpening());
        dto.setModeOfOperation(account.getModeOfOperation());
        
        // *** THE FIX IS HERE: Changed from is...() to get...() ***
        dto.setNomineeRegistered(account.getNomineeRegistered());
        dto.setNomineeName(account.getNomineeName());
        dto.setNetBankingEnabled(account.getNetBankingEnabled());
        dto.setDebitCardIssued(account.getDebitCardIssued());
        dto.setDebitCardLast4Digits(account.getDebitCardLast4Digits());
        dto.setChequeBookIssued(account.getChequeBookIssued());
        
        dto.setChequeBookLeaves(account.getChequeBookLeaves());
        
        return dto;
    }
    
 // *** ADD THIS NEW ENDPOINT ***
    @GetMapping("/by-kyc-id/{kycId}")
    public ResponseEntity<AccountDTO> getAccountByKycApplicationId(@PathVariable Long kycId) {
        return accountRepository.findByKycApplicationId(kycId)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ... The rest of the controller remains the same ...
    @PostMapping("/create-inactive")
    public ResponseEntity<AccountDTO> createInactiveAccount(@RequestBody Map<String, Object> creationData) {
        // The service now handles the enriched map, so no change is needed here.
        Account account = accountService.createInactiveAccount(creationData);
        return ResponseEntity.ok(convertToDto(account));
    }

    @PostMapping("/customer/{customerId}/activate")
    public ResponseEntity<AccountDTO> activateAccount(@PathVariable Long customerId) {
        // The service now handles populating final details, so no change is needed here.
        Account account = accountService.activateAccount(customerId);
        return ResponseEntity.ok(convertToDto(account));
    }

    // No changes needed for the remaining GET methods as they rely on the updated convertToDto helper.
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<AccountDTO> getAccountByCustomerId(@PathVariable Long customerId) {
        return accountRepository.findByCustomerId(customerId)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-customer-ids")
    public ResponseEntity<List<AccountDTO>> getAccountsByCustomerIds(@RequestParam("customerIds") List<Long> customerIds) {
        return ResponseEntity.ok(accountRepository.findByCustomerIdIn(customerIds).stream()
                .map(this::convertToDto).collect(Collectors.toList()));
    }

    @GetMapping("/created-between")
    public ResponseEntity<List<AccountDTO>> getAccountsCreatedBetween(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(accountRepository.findByCreatedAtBetween(start, end).stream()
                .map(this::convertToDto).collect(Collectors.toList()));
    }
    
    @PutMapping("/customer/{customerId}")
    public ResponseEntity<AccountDTO> updateAccountDetails(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> updateData) {
        
        Account updatedAccount = accountService.updateAccountDetails(customerId, updateData);
        return ResponseEntity.ok(convertToDto(updatedAccount));
    }
    
    @PostMapping("/customer/{customerId}/deactivate")
    public ResponseEntity<AccountDTO> deactivateAccount(@PathVariable Long customerId) {
        Account account = accountService.deactivateAccount(customerId);
        return ResponseEntity.ok(convertToDto(account));
    }

}