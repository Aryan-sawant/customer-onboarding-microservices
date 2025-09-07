package com.onboarding.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // <-- IMPORT THIS
import jakarta.persistence.*;

@Entity
@Table(name = "nominee")
public class Nominee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nominee_seq")
    @SequenceGenerator(name = "nominee_seq", sequenceName = "nominee_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false)
    private String address;

    @Column(name = "aadhaar_number", nullable = false, unique = false)
    private String aadhaarNumber;

    /**
     * *** THE FIX IS HERE ***
     * Add @JsonBackReference to prevent infinite recursion during JSON serialization.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    @JsonBackReference
    private Customer customer;

    // --- Manual Getters and Setters ---
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
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    
    @Override
    public String toString() {
        return "Nominee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}