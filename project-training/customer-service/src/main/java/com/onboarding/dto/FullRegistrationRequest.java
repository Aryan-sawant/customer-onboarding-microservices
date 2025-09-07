package com.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class FullRegistrationRequest {

    // --- Personal Details ---
    @NotBlank(message = "Full name is required.")
    private String fullname;
    
    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter a valid 10-digit Indian mobile number.")
    private String phone;
    
    @NotNull(message = "Date of birth is required.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dob;
    
    private String address;
    private String gender;
    private String maritalStatus;
    private String fathersName;
    private String nationality;
    private String profession;
    
    // --- Identity ---
    @NotBlank(message = "PAN is required.")
    @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN format.")
    private String pan;

    @NotBlank(message = "Aadhaar is required.")
    @Size(min = 12, max = 12, message = "Aadhaar number must be 12 digits.")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar number must contain only digits.")
    private String aadhaar;

    // --- Login Details ---
    @NotBlank(message = "Username is required.")
    @Size(min = 5, message = "Username must be at least 5 characters long.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;
    
    // --- Account Choice ---
    @NotBlank(message = "You must select an account type.")
    private String requestedAccountType;

    // --- Nested DTO for Nominee ---
    @Valid // This enables validation on the nested object
    private NomineeDTO nominee; // Can be null if the user chooses not to add one

    // --- Service Preferences ---
    private boolean netBankingEnabled;
    private boolean debitCardIssued;
    private boolean chequeBookIssued;

    // Getters and Setters for all fields...
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
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
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRequestedAccountType() { return requestedAccountType; }
    public void setRequestedAccountType(String requestedAccountType) { this.requestedAccountType = requestedAccountType; }
    public NomineeDTO getNominee() { return nominee; }
    public void setNominee(NomineeDTO nominee) { this.nominee = nominee; }
    public boolean isNetBankingEnabled() { return netBankingEnabled; }
    public void setNetBankingEnabled(boolean netBankingEnabled) { this.netBankingEnabled = netBankingEnabled; }
    public boolean isDebitCardIssued() { return debitCardIssued; }
    public void setDebitCardIssued(boolean debitCardIssued) { this.debitCardIssued = debitCardIssued; }
    public boolean isChequeBookIssued() { return chequeBookIssued; }
    public void setChequeBookIssued(boolean chequeBookIssued) { this.chequeBookIssued = chequeBookIssued; }
}