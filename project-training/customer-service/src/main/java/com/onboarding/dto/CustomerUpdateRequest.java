package com.onboarding.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;

public class CustomerUpdateRequest {

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
    
    @Valid // Enable validation for the nested nominee object
    private NomineeDTO nominee;

    // --- Getters and Setters ---
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public NomineeDTO getNominee() { return nominee; }
    public void setNominee(NomineeDTO nominee) { this.nominee = nominee; }
}