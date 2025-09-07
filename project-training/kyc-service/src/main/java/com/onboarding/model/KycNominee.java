package com.onboarding.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "KYC_NOMINEES")
public class KycNominee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String address;

    @Column(name = "aadhaar_number", nullable = false, unique = false)
    private String aadhaarNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kyc_application_id", nullable = false, unique = true)
    @JsonManagedReference
    private KycApplication kycApplication;

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
    public KycApplication getKycApplication() { return kycApplication; }
    public void setKycApplication(KycApplication kycApplication) { this.kycApplication = kycApplication; }
}