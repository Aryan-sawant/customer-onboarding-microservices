// src/main/java/com/onboarding/dto/AccountCreationRequest.java
package com.onboarding.dto;

import java.math.BigDecimal;

public class AccountCreationRequest {
    private Long customerId;
    private String accountType;
    private BigDecimal initialDeposit; // <-- ADD THIS LINE

    // Add getters and setters for all fields
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }
}