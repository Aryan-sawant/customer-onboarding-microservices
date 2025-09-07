package com.onboarding.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "KYC_APPLICATIONS")
public class KycApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Personal & Contact Info (Existing fields are correct) ---
    @NotBlank(message = "Full name is mandatory")
    private String fullName;
    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dob;
    @NotBlank(message = "Gender is mandatory")
    private String gender;
    @NotBlank(message = "Marital status is mandatory")
    private String maritalStatus;
    @NotBlank(message = "Father's name is mandatory")
    private String fathersName;
    @NotBlank(message = "Nationality is mandatory")
    private String nationality;
    @NotBlank(message = "Profession is mandatory")
    private String profession;
    @NotBlank(message = "Address is mandatory")
    private String address;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phone;
    @NotBlank(message = "PAN is mandatory")
    @Column(unique = true)
    private String pan;
    @NotBlank(message = "Aadhaar is mandatory")
    @Column(unique = true)
    private String aadhaar;

    // --- Document Uploads (Existing fields are correct) ---
    @Lob @Column(name = "aadhaar_photo_base64", columnDefinition = "CLOB")
    private String aadhaarPhotoBase64;
    @Lob @Column(name = "pan_photo_base64", columnDefinition = "CLOB")
    private String panPhotoBase64;

    // --- Account & Login Choices (Existing fields are correct) ---
    @NotBlank(message = "Account type selection is mandatory")
    private String requestedAccountType;
    @NotBlank(message = "Username is mandatory")
    @Column(unique = true)
    private String username;
    @NotBlank(message = "Password is mandatory")
    private String password;

    // *** NEWLY ADDED FIELDS FOR SERVICE PREFERENCES ***
    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean netBankingEnabled = false;
    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean debitCardIssued = false;
    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean chequeBookIssued = false;

    // *** NEWLY ADDED FIELD TO LINK TO FINAL CUSTOMER RECORD ***
    private Long customerId;

    // --- Internal Status ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus kycStatus = KycStatus.PENDING;
    
 // In KycApplication.java

 // ... after your existing Base64 fields ...

 @Lob @Column(name = "passport_photo_base64", columnDefinition = "CLOB")
 private String passportPhotoBase64;
 
 @Column(nullable = false, updatable = false)
 @CreationTimestamp // This tells Hibernate to set the timestamp automatically on creation
 private LocalDateTime createdAt;
 

 // --- ADD THESE 3 NEW FIELDS ---
 private String passportPhotoContentType;
 private String panPhotoContentType;
 private String aadhaarPhotoContentType;


 // --- AND ADD THEIR GETTERS AND SETTERS AT THE BOTTOM ---

 public String getPassportPhotoContentType() {
     return passportPhotoContentType;
 }

 public void setPassportPhotoContentType(String passportPhotoContentType) {
     this.passportPhotoContentType = passportPhotoContentType;
 }

 public String getPanPhotoContentType() {
     return panPhotoContentType;
 }

 public void setPanPhotoContentType(String panPhotoContentType) {
     this.panPhotoContentType = panPhotoContentType;
 }

 public String getAadhaarPhotoContentType() {
     return aadhaarPhotoContentType;
 }

 public void setAadhaarPhotoContentType(String aadhaarPhotoContentType) {
     this.aadhaarPhotoContentType = aadhaarPhotoContentType;
 }
    
 // *** NEW RELATIONSHIP MAPPING ***
    @OneToOne(mappedBy = "kycApplication", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private KycNominee kycNominee;

    // --- Getters and Setters for all fields (including new ones) ---
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
    public String getAadhaarPhotoBase64() { return aadhaarPhotoBase64; }
    public void setAadhaarPhotoBase64(String aadhaarPhotoBase64) { this.aadhaarPhotoBase64 = aadhaarPhotoBase64; }
    public String getPanPhotoBase64() { return panPhotoBase64; }
    public void setPanPhotoBase64(String panPhotoBase64) { this.panPhotoBase64 = panPhotoBase64; }
    public String getPassportPhotoBase64() { return passportPhotoBase64; }
    public void setPassportPhotoBase64(String passportPhotoBase64) { this.passportPhotoBase64 = passportPhotoBase64; }
    public String getRequestedAccountType() { return requestedAccountType; }
    public void setRequestedAccountType(String requestedAccountType) { this.requestedAccountType = requestedAccountType; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public KycStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KycStatus kycStatus) { this.kycStatus = kycStatus; }
    public Boolean getNetBankingEnabled() { return netBankingEnabled; }
    public void setNetBankingEnabled(Boolean netBankingEnabled) { this.netBankingEnabled = netBankingEnabled; }
    public Boolean getDebitCardIssued() { return debitCardIssued; }
    public void setDebitCardIssued(Boolean debitCardIssued) { this.debitCardIssued = debitCardIssued; }
    public Boolean getChequeBookIssued() { return chequeBookIssued; }
    public void setChequeBookIssued(Boolean chequeBookIssued) { this.chequeBookIssued = chequeBookIssued; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public KycNominee getKycNominee() { return kycNominee; }

    public void setKycNominee(KycNominee kycNominee) {
        if (kycNominee == null) {
            if (this.kycNominee != null) {
                this.kycNominee.setKycApplication(null);
            }
        } else {
            kycNominee.setKycApplication(this);
        }
        this.kycNominee = kycNominee;
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}