package com.onboarding.dto;

import java.io.Serializable;

// This class represents an event received from the kyc-service.
// Its structure must exactly match the version in the producing service.
public class NewKycApplicationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long kycApplicationId;
    private String applicantName;
    private String applicantEmail;
    
    // *** NEW FIELD to match the producer's version ***
    private boolean isReapplication = false;

    // A no-argument constructor is needed for deserialization
    public NewKycApplicationEvent() {
    }

    // Original constructor for brand new applications
    public NewKycApplicationEvent(Long kycApplicationId, String applicantName, String applicantEmail) {
        this.kycApplicationId = kycApplicationId;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.isReapplication = false; // Default for new applications
    }
    
    // *** NEW CONSTRUCTOR to fully match the producer's version ***
    public NewKycApplicationEvent(Long kycApplicationId, String applicantName, String applicantEmail, boolean isReapplication) {
        this.kycApplicationId = kycApplicationId;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.isReapplication = isReapplication;
    }

    // --- Getters and Setters for all fields ---
    public Long getKycApplicationId() {
        return kycApplicationId;
    }

    public void setKycApplicationId(Long kycApplicationId) {
        this.kycApplicationId = kycApplicationId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public boolean isReapplication() {
        return isReapplication;
    }

    public void setReapplication(boolean reapplication) {
        isReapplication = reapplication;
    }

    @Override
    public String toString() {
        return "NewKycApplicationEvent{" +
                "kycApplicationId=" + kycApplicationId +
                ", applicantName='" + applicantName + '\'' +
                ", applicantEmail='" + applicantEmail + '\'' +
                ", isReapplication=" + isReapplication +
                '}';
    }
}