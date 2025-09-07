package com.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AdminCustomerUpdateRequest {

    // These fields are identical to the customer's update request
    @NotBlank(message = "Full name is required.")
    private String fullName;
    
    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter a valid 10-digit Indian mobile number.")
    private String phone;
    
    @NotBlank(message = "Address is required.")
    private String address;
    
    @NotBlank(message = "Marital status is required.")
    private String maritalStatus;
    
    // Admins can also update these fields
    private String profession;
    private String fathersName;

    @Valid
    private NomineeDTO nominee;

    // --- Getters and Setters ---
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getFathersName() { return fathersName; }
    public void setFathersName(String fathersName) { this.fathersName = fathersName; }
    public NomineeDTO getNominee() { return nominee; }
    public void setNominee(NomineeDTO nominee) { this.nominee = nominee; }
}