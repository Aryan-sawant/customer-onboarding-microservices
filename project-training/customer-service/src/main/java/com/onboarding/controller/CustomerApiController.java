package com.onboarding.controller;

import com.onboarding.dto.CustomerDTO;
import com.onboarding.dto.CustomerUpdateRequest;
import com.onboarding.model.Customer;
import com.onboarding.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerApiController {

    private final CustomerService customerService;

    public CustomerApiController(CustomerService customerService) {
        this.customerService = customerService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerDetails(@PathVariable Long id) {
        // We can reuse the AdminApiController's logic to convert the entity to a DTO.
        // For simplicity, we'll just fetch and assume a converter exists.
        // A better approach would be to have a shared converter utility.
        return customerService.findCustomerById(id)
                .map(customer -> customerService.convertToDto(customer)) // We'll create this public helper
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/find-by-pan")
    public ResponseEntity<Customer> findCustomerByPan(@RequestParam String pan) {
        return customerService.findCustomerByPan(pan)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateApprovedCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {
        try {
            Customer updatedCustomer = customerService.updateApprovedCustomer(id, request);
            return ResponseEntity.ok(updatedCustomer);
        } catch (Exception e) {
            // In a real app, handle exceptions more gracefully
            return ResponseEntity.badRequest().build();
        }
    }
}