package com.onboarding.feign;

import com.onboarding.dto.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// The name "CUSTOMER-SERVICE" must match the spring.application.name in customer-service's properties
@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    /**
     * Calls an internal API on the customer-service to get customer details.
     * We will need to create this endpoint in the customer-service.
     */
    @GetMapping("/api/internal/customers/{id}")
    CustomerDTO getCustomerById(@PathVariable("id") Long id);
}