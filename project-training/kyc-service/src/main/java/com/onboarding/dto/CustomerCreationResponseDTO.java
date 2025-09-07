package com.onboarding.dto;

public class CustomerCreationResponseDTO {
    private Long id;
    private String fullname;

    // A default constructor is needed for deserialization
    public CustomerCreationResponseDTO() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}