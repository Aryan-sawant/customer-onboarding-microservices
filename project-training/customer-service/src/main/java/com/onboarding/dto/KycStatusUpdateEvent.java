package com.onboarding.dto;

import java.io.Serializable;

public class KycStatusUpdateEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String customerName;
    private String customerEmail;
    private String kycStatus; // "VERIFIED" or "REJECTED"

    // Fields for the approval email
    private String accountNumber;
    private String accountType;
    private String ifscCode;

    // --- THIS IS THE MISSING FIELD ---
    private String rejectionReason;

    // A no-argument constructor is needed for deserialization
    public KycStatusUpdateEvent() {
    }

    // Getters and Setters for all fields...

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    // --- GETTER AND SETTER FOR THE MISSING FIELD ---
    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}