package com.onboarding.dto;

import java.time.LocalDate;

public class CustomerDTO {
    // A comprehensive DTO to hold all customer data for review
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String address;
    private String gender;
    private String maritalStatus;
    private String fathersName;
    private String nationality;
    private String profession;
    private String pan;
    private String aadhaar;
    private String kycStatus;
    private String requestedAccountType;
    private boolean netBankingEnabled;
    private boolean debitCardIssued;
    private boolean chequeBookIssued;
    
    // Document Fields
    private String passportPhotoBase64;
    private String panPhotoBase64;
    private String panPhotoContentType;
    private String aadhaarPhotoBase64;
    private String aadhaarPhotoContentType;
    private NomineeDTO nominee;

    // *** THE FIX: Add the missing field for the passport photo's content type ***
    private String passportPhotoContentType;


    // Manual Getters and Setters for all fields...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
	public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public String getFathersName() { return fathersName; }
    public void setFathersName(String fathersName) { this.fathersName = fathersName; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getAadhaar() { return aadhaar; }
    public void setAadhaar(String aadhaar) { this.aadhaar = aadhaar; }
    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
    public String getRequestedAccountType() { return requestedAccountType; }
    public void setRequestedAccountType(String requestedAccountType) { this.requestedAccountType = requestedAccountType; }
    public boolean isNetBankingEnabled() { return netBankingEnabled; }
    public void setNetBankingEnabled(boolean netBankingEnabled) { this.netBankingEnabled = netBankingEnabled; }
    public boolean isDebitCardIssued() { return debitCardIssued; }
    public void setDebitCardIssued(boolean debitCardIssued) { this.debitCardIssued = debitCardIssued; }
    public boolean isChequeBookIssued() { return chequeBookIssued; }
    public void setChequeBookIssued(boolean chequeBookIssued) { this.chequeBookIssued = chequeBookIssued; }
    public String getPassportPhotoBase64() { return passportPhotoBase64; }
    public void setPassportPhotoBase64(String passportPhotoBase64) { this.passportPhotoBase64 = passportPhotoBase64; }
    public String getPanPhotoBase64() { return panPhotoBase64; }
    public void setPanPhotoBase64(String panPhotoBase64) { this.panPhotoBase64 = panPhotoBase64; }
    public String getPanPhotoContentType() { return panPhotoContentType; }
    public void setPanPhotoContentType(String panPhotoContentType) { this.panPhotoContentType = panPhotoContentType; }
    public String getAadhaarPhotoBase64() { return aadhaarPhotoBase64; }
    public void setAadhaarPhotoBase64(String aadhaarPhotoBase64) { this.aadhaarPhotoBase64 = aadhaarPhotoBase64; }
    public String getAadhaarPhotoContentType() { return aadhaarPhotoContentType; }
    public void setAadhaarPhotoContentType(String aadhaarPhotoContentType) { this.aadhaarPhotoContentType = aadhaarPhotoContentType; }
    public NomineeDTO getNominee() { return nominee; }
    public void setNominee(NomineeDTO nominee) { this.nominee = nominee; }

    // *** THE FIX: Add the getter and setter for the new field ***
    public String getPassportPhotoContentType() {
        return passportPhotoContentType;
    }
    public void setPassportPhotoContentType(String passportPhotoContentType) {
        this.passportPhotoContentType = passportPhotoContentType;
    }
}