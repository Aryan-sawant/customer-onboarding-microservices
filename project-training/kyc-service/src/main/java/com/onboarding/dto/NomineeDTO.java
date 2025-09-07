package com.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NomineeDTO {
    @NotBlank(message = "Nominee name is required.")
    private String name;
    @NotBlank(message = "Nominee mobile number is required.")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please enter a valid 10-digit Indian mobile number.")
    private String mobile;
    @NotBlank(message = "Nominee address is required.")
    private String address;
    @NotBlank(message = "Nominee Aadhaar number is required.")
    @Size(min = 12, max = 12, message = "Aadhaar number must be 12 digits.")
    @Pattern(regexp = "\\d{12}", message = "Aadhaar number must contain only digits.")
    private String aadhaarNumber;

    // Manual Getters and Setters...
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
}