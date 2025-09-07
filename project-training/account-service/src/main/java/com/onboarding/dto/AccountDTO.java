package com.onboarding.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDTO {

    private Long customerId;
    private String accountNumber;
    private String accountType;
    private String accountStatus;
    private BigDecimal balance;
    
    // --- All Account Detail Fields ---
    private String branchName;
    private String ifscCode;
    private String micrCode;
    private LocalDateTime dateOfAccountOpening;
    private String modeOfOperation;
    private Boolean nomineeRegistered;
    private String nomineeName;
    private Boolean netBankingEnabled;
    private Boolean debitCardIssued;
    private String debitCardLast4Digits;
    private Boolean chequeBookIssued;
    private Integer chequeBookLeaves;

    // --- Manual Getters and Setters for ALL fields ---

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getMicrCode() { return micrCode; }
    public void setMicrCode(String micrCode) { this.micrCode = micrCode; }
    public LocalDateTime getDateOfAccountOpening() { return dateOfAccountOpening; }
    public void setDateOfAccountOpening(LocalDateTime dateOfAccountOpening) { this.dateOfAccountOpening = dateOfAccountOpening; }
    public String getModeOfOperation() { return modeOfOperation; }
    public void setModeOfOperation(String modeOfOperation) { this.modeOfOperation = modeOfOperation; }
    public Boolean getNomineeRegistered() { return nomineeRegistered; }
    public void setNomineeRegistered(Boolean nomineeRegistered) { this.nomineeRegistered = nomineeRegistered; }
    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }
    public Boolean getNetBankingEnabled() { return netBankingEnabled; }
    public void setNetBankingEnabled(Boolean netBankingEnabled) { this.netBankingEnabled = netBankingEnabled; }
    public Boolean getDebitCardIssued() { return debitCardIssued; }
    public void setDebitCardIssued(Boolean debitCardIssued) { this.debitCardIssued = debitCardIssued; }
    public String getDebitCardLast4Digits() { return debitCardLast4Digits; }
    public void setDebitCardLast4Digits(String debitCardLast4Digits) { this.debitCardLast4Digits = debitCardLast4Digits; }
    public Boolean getChequeBookIssued() { return chequeBookIssued; }
    public void setChequeBookIssued(Boolean chequeBookIssued) { this.chequeBookIssued = chequeBookIssued; }
    public Integer getChequeBookLeaves() { return chequeBookLeaves; }
    public void setChequeBookLeaves(Integer chequeBookLeaves) { this.chequeBookLeaves = chequeBookLeaves; }
}