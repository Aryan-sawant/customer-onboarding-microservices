package com.onboarding.dto;

import java.time.LocalDate;

public class KycApplicationDataDTO {

    // ... all existing fields (id, fullName, dob, etc.) ...
    private Long id;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String maritalStatus;
    private String fathersName;
    private String nationality;
    private String profession;
    private String address;
    private String email;
    private String phone;
    private String pan;
    private String aadhaar;
    private String username;
    private String password;
    private String requestedAccountType;
    private Boolean netBankingEnabled;
    private Boolean debitCardIssued;
    private Boolean chequeBookIssued;
    private NomineeDTO nominee;
    
    // *** NEWLY ADDED FIELDS FOR PHOTOS ***
    private String passportPhotoBase64;
    private String panPhotoBase64;
    private String panPhotoContentType;
    private String aadhaarPhotoBase64;
    private String aadhaarPhotoContentType;

    // --- Getters and Setters for ALL fields, including new ones ---
    // ... all existing getters and setters ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
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
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getAadhaar() { return aadhaar; }
    public void setAadhaar(String aadhaar) { this.aadhaar = aadhaar; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRequestedAccountType() { return requestedAccountType; }
    public void setRequestedAccountType(String requestedAccountType) { this.requestedAccountType = requestedAccountType; }
    public Boolean getNetBankingEnabled() { return netBankingEnabled; }
    public void setNetBankingEnabled(Boolean netBankingEnabled) { this.netBankingEnabled = netBankingEnabled; }
    public Boolean getDebitCardIssued() { return debitCardIssued; }
    public void setDebitCardIssued(Boolean debitCardIssued) { this.debitCardIssued = debitCardIssued; }
    public Boolean getChequeBookIssued() { return chequeBookIssued; }
    public void setChequeBookIssued(Boolean chequeBookIssued) { this.chequeBookIssued = chequeBookIssued; }
    
    // Getters and Setters for photo fields
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
}