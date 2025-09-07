package com.onboarding.feign;

import com.onboarding.dto.AdminCustomerUpdateRequest;
import com.onboarding.dto.CustomerCreationResponseDTO;
import com.onboarding.dto.CustomerDTO;
import com.onboarding.dto.CustomerUpdateRequest;
import com.onboarding.dto.KycApplicationDataDTO;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    /**
     * Calls the internal API on customer-service to create a new, fully approved customer.
     * This now expects to receive the lightweight CustomerCreationResponseDTO upon success.
     */
    @PostMapping("/api/internal/customers/create-from-kyc")
    CustomerCreationResponseDTO createApprovedCustomer(@RequestBody KycApplicationDataDTO kycData);
    
    @PutMapping("/api/customers/{id}")
    CustomerDTO updateApprovedCustomer(@PathVariable("id") Long id, @RequestBody CustomerUpdateRequest request);
    
    @GetMapping("/api/customers/find-by-pan")
    CustomerDTO findCustomerByPan(@RequestParam("pan") String pan);
    
    @GetMapping("/api/customers/{id}")
    CustomerDTO getApprovedCustomerById(@PathVariable("id") Long id);
    
    @PutMapping("/api/admin/customers/{id}")
    CustomerDTO updateCustomerByAdmin(@PathVariable("id") Long id, @RequestBody AdminCustomerUpdateRequest request);
    

    @GetMapping("/api/admin/customers/{id}")
    CustomerDTO getCustomerById(@PathVariable("id") Long id);
    
    @GetMapping("/api/admin/customers/search")
    List<CustomerDTO> searchCustomers(@RequestParam("keyword") String keyword);
    
}