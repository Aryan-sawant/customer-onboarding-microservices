package com.onboarding.controller;

import com.onboarding.dto.AdminCustomerUpdateRequest;
import com.onboarding.dto.CustomerDTO;
import com.onboarding.dto.NomineeDTO;
import com.onboarding.model.Customer;
import com.onboarding.service.CustomerService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/customers")
public class AdminApiController {

    private final CustomerService customerService;

    public AdminApiController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getCustomers(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Page<Customer> customerPage = (keyword == null || keyword.isBlank())
                ? customerService.findAllCustomers(pageable)
                : customerService.searchCustomers(keyword, pageable);
        
        return ResponseEntity.ok(customerPage.map(this::convertToDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return customerService.findCustomerById(id)
                .map(this::convertToDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private CustomerDTO convertToDto(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setDob(customer.getDob());
        dto.setAddress(customer.getAddress());
        dto.setGender(customer.getGender());
        dto.setMaritalStatus(customer.getMaritalStatus());
        dto.setFathersName(customer.getFathersName());
        dto.setNationality(customer.getNationality());
        dto.setProfession(customer.getProfession());
        dto.setPan(customer.getPan());
        dto.setAadhaar(customer.getAadhaar());
        dto.setKycStatus(customer.getKycStatus().name());
        dto.setRequestedAccountType(customer.getRequestedAccountType());
        
        // *** THE FIX IS HERE: Changed from is...() to get...() ***
        dto.setNetBankingEnabled(customer.getNetBankingEnabled());
        dto.setDebitCardIssued(customer.getDebitCardIssued());
        dto.setChequeBookIssued(customer.getChequeBookIssued());
        
        dto.setPassportPhotoBase64(customer.getPassportPhotoBase64());
        dto.setPanPhotoBase64(customer.getPanPhotoBase64());
        dto.setPanPhotoContentType(customer.getPanPhotoContentType());
        dto.setAadhaarPhotoBase64(customer.getAadhaarPhotoBase64());
        dto.setAadhaarPhotoContentType(customer.getAadhaarPhotoContentType());

        if (customer.getNominee() != null) {
            NomineeDTO nomineeDTO = new NomineeDTO();
            nomineeDTO.setName(customer.getNominee().getName());
            nomineeDTO.setMobile(customer.getNominee().getMobile());
            nomineeDTO.setAddress(customer.getNominee().getAddress());
            nomineeDTO.setAadhaarNumber(customer.getNominee().getAadhaarNumber());
            dto.setNominee(nomineeDTO);
        }
        return dto;
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomerByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminCustomerUpdateRequest request) {
        
        Customer updatedCustomer = customerService.updateCustomerByAdmin(id, request);
        return ResponseEntity.ok(customerService.convertToDto(updatedCustomer));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam String keyword) {
        // We use Pageable.unpaged() to get all results without pagination
        Page<Customer> results = customerService.searchByKeyword(keyword, Pageable.unpaged());
        List<CustomerDTO> dtoList = results.getContent().stream()
                                           .map(this::convertToDto)
                                           .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }
}