package com.onboarding.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_id_seq", allocationSize = 1)
    private Long id;

    // --- Personal Details ---
    @Column(nullable = false)
    private String fullName;

    // ... other personal detail fields are fine ...
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String phone;
    @Column(nullable = false)
    private LocalDate dob;
    private String address;
    private String gender;
    private String maritalStatus;
    private String fathersName;
    private String nationality;
    private String profession;
    @Column(unique = true, nullable = false)
    private String pan;
    @Column(unique = true, nullable = false)
    private String aadhaar;

    // --- System Status & Flags ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KycStatus kycStatus = KycStatus.PENDING;

    // *** THE FIX IS HERE ***
    // Use Boolean wrapper and define the column correctly for Oracle
    @Column(name = "pan_linked", columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean panLinked = false;

    @Column(name = "aadhaar_linked", columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean aadhaarLinked = false;

    // --- Service Preferences (Also Fixed) ---
    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean netBankingEnabled = false;

    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean debitCardIssued = false;

    @Column(columnDefinition = "NUMBER(1,0) DEFAULT 0")
    private Boolean chequeBookIssued = false;
    
    // --- Other fields are fine ---
    private String requestedAccountType;
    @Lob
    @Column(name = "passport_photo_base64", columnDefinition = "CLOB")
    private String passportPhotoBase64;
    @Column(name = "passport_photo_content_type")
    private String passportPhotoContentType;
    @Lob
    @Column(name = "pan_photo_base64", columnDefinition = "CLOB")
    private String panPhotoBase64;
    @Column(name = "pan_photo_content_type")
    private String panPhotoContentType;
    @Lob
    @Column(name = "aadhaar_photo_base64", columnDefinition = "CLOB")
    private String aadhaarPhotoBase64;
    @Column(name = "aadhaar_photo_content_type")
    private String aadhaarPhotoContentType;
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private Nominee nominee;

    // --- All Getters and Setters for the new Boolean types ---
    // (Ensure you update these from isPanLinked() to getPanLinked() etc. if needed by your logic)

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
    public KycStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KycStatus kycStatus) { this.kycStatus = kycStatus; }
    public Boolean getPanLinked() { return panLinked; }
    public void setPanLinked(Boolean panLinked) { this.panLinked = panLinked; }
    public Boolean getAadhaarLinked() { return aadhaarLinked; }
    public void setAadhaarLinked(Boolean aadhaarLinked) { this.aadhaarLinked = aadhaarLinked; }
    public Boolean getNetBankingEnabled() { return netBankingEnabled; }
    public void setNetBankingEnabled(Boolean netBankingEnabled) { this.netBankingEnabled = netBankingEnabled; }
    public Boolean getDebitCardIssued() { return debitCardIssued; }
    public void setDebitCardIssued(Boolean debitCardIssued) { this.debitCardIssued = debitCardIssued; }
    public Boolean getChequeBookIssued() { return chequeBookIssued; }
    public void setChequeBookIssued(Boolean chequeBookIssued) { this.chequeBookIssued = chequeBookIssued; }
    public String getRequestedAccountType() { return requestedAccountType; }
    public void setRequestedAccountType(String requestedAccountType) { this.requestedAccountType = requestedAccountType; }
    public String getPassportPhotoBase64() { return passportPhotoBase64; }
    public void setPassportPhotoBase64(String passportPhotoBase64) { this.passportPhotoBase64 = passportPhotoBase64; }
    public String getPassportPhotoContentType() { return passportPhotoContentType; }
    public void setPassportPhotoContentType(String passportPhotoContentType) { this.passportPhotoContentType = passportPhotoContentType; }
    public String getPanPhotoBase64() { return panPhotoBase64; }
    public void setPanPhotoBase64(String panPhotoBase64) { this.panPhotoBase64 = panPhotoBase64; }
    public String getPanPhotoContentType() { return panPhotoContentType; }
    public void setPanPhotoContentType(String panPhotoContentType) { this.panPhotoContentType = panPhotoContentType; }
    public String getAadhaarPhotoBase64() { return aadhaarPhotoBase64; }
    public void setAadhaarPhotoBase64(String aadhaarPhotoBase64) { this.aadhaarPhotoBase64 = aadhaarPhotoBase64; }
    public String getAadhaarPhotoContentType() { return aadhaarPhotoContentType; }
    public void setAadhaarPhotoContentType(String aadhaarPhotoContentType) { this.aadhaarPhotoContentType = aadhaarPhotoContentType; }
    public Nominee getNominee() { return nominee; }
    public void setNominee(Nominee nominee) {
        if (nominee != null) {
            nominee.setCustomer(this);
        }
        this.nominee = nominee;
    }
}