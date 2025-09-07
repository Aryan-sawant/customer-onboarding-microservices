package com.onboarding.dto;

// This is a sample DTO. Ensure your actual DTO has these fields and getters.
public class AccountDTO {
    private String accountNumber;
    private String accountType;
    private String ifscCode; // The field that was missing a getter
    private Long customerId;

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public String getIfscCode() { return ifscCode; } // The required getter
    public Long getCustomerId() { return customerId; }

    // Setters
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
}