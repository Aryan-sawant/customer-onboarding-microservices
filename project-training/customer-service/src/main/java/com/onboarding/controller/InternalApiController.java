package com.onboarding.controller;

import com.onboarding.dto.CustomerCreationResponseDTO;
import com.onboarding.dto.KycApplicationDataDTO;
import com.onboarding.model.Customer;
import com.onboarding.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/customers")
public class InternalApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InternalApiController.class);
    private final CustomerService customerService;

    public InternalApiController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * This endpoint is called by the kyc-service ONLY AFTER an admin has approved an application.
     * It receives the full application data, creates the permanent records, and returns ONLY the new ID and name.
     */
    @PostMapping("/create-from-kyc")
    public ResponseEntity<CustomerCreationResponseDTO> createApprovedCustomer(@RequestBody KycApplicationDataDTO kycData) {
        LOGGER.info("Received request from kyc-service to create approved customer for application ID: {}", kycData.getId());
        try {
            Customer newCustomer = customerService.createApprovedCustomer(kycData);
            
            // Create the lightweight response object to avoid sending large Base64 strings back
            CustomerCreationResponseDTO response = new CustomerCreationResponseDTO();
            response.setId(newCustomer.getId());
            response.setFullname(newCustomer.getFullName());
            
            LOGGER.info("Successfully created approved customer with ID: {}", newCustomer.getId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            LOGGER.error("Failed to create approved customer for app ID {}. Error: {}", kycData.getId(), e.getMessage());
            // Return null body with a bad request status to indicate failure
            return ResponseEntity.badRequest().body(null);
        }
    }
}