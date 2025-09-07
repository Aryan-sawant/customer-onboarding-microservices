package com.onboarding.service;

import com.onboarding.dto.AdminCustomerUpdateRequest;
import com.onboarding.dto.CustomerDTO;
import com.onboarding.dto.CustomerUpdateRequest;
import com.onboarding.dto.KycApplicationDataDTO;
import com.onboarding.dto.NomineeDTO;
import com.onboarding.exception.CustomerAlreadyExistsException;
import com.onboarding.model.*;
import com.onboarding.repository.CustomerRepository;
import com.onboarding.repository.NomineeRepository;
import com.onboarding.repository.RoleRepository;
import com.onboarding.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NomineeRepository nomineeRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository, RoleRepository roleRepository, NomineeRepository nomineeRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.nomineeRepository = nomineeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Customer createApprovedCustomer(KycApplicationDataDTO kycData) {
        // Validation for uniqueness
        if (customerRepository.findByPan(kycData.getPan()).isPresent() ||
            userRepository.findByUsername(kycData.getUsername()).isPresent() ||
            customerRepository.findByAadhaar(kycData.getAadhaar()).isPresent()) {
            throw new CustomerAlreadyExistsException("A customer or user with these unique details already exists.");
        }
        
        if (kycData.getNominee() != null) {
            nomineeRepository.findByAadhaarNumber(kycData.getNominee().getAadhaarNumber()).ifPresent(n -> {
                 throw new CustomerAlreadyExistsException("A nominee with that Aadhaar number is already registered.");
            });
        }

        Customer customer = new Customer();
        
        // --- Map ALL fields from the DTO to the Customer entity ---
        customer.setFullName(kycData.getFullName());
        customer.setDob(kycData.getDob());
        customer.setGender(kycData.getGender());
        customer.setMaritalStatus(kycData.getMaritalStatus());
        customer.setFathersName(kycData.getFathersName());
        customer.setNationality(kycData.getNationality());
        customer.setProfession(kycData.getProfession());
        customer.setAddress(kycData.getAddress());
        customer.setEmail(kycData.getEmail());
        customer.setPhone(kycData.getPhone());
        customer.setPan(kycData.getPan());
        customer.setAadhaar(kycData.getAadhaar());
        customer.setRequestedAccountType(kycData.getRequestedAccountType());
        customer.setNetBankingEnabled(kycData.getNetBankingEnabled());
        customer.setDebitCardIssued(kycData.getDebitCardIssued());
        customer.setChequeBookIssued(kycData.getChequeBookIssued());
        customer.setPassportPhotoBase64(kycData.getPassportPhotoBase64());
        customer.setPanPhotoBase64(kycData.getPanPhotoBase64());
        customer.setPanPhotoContentType(kycData.getPanPhotoContentType());
        customer.setAadhaarPhotoBase64(kycData.getAadhaarPhotoBase64());
        customer.setAadhaarPhotoContentType(kycData.getAadhaarPhotoContentType());
        
        customer.setKycStatus(KycStatus.VERIFIED);
        customer.setPanLinked(true);
        customer.setAadhaarLinked(true);

        // *** THE FIX: Handle the nominee if it exists in the DTO ***
        if (kycData.getNominee() != null) {
            Nominee nominee = new Nominee();
            NomineeDTO nomineeDTO = kycData.getNominee();
            nominee.setName(nomineeDTO.getName());
            nominee.setMobile(nomineeDTO.getMobile());
            nominee.setAddress(nomineeDTO.getAddress());
            nominee.setAadhaarNumber(nomineeDTO.getAadhaarNumber());
            customer.setNominee(nominee); // This links the two entities
        }

        Customer savedCustomer = customerRepository.save(customer);

        // Create the associated User entity for login
        User user = new User();
        user.setUsername(kycData.getUsername());
        user.setPassword(kycData.getPassword());
        
        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("CRITICAL: ROLE_CUSTOMER not found."));
        user.setRoles(Set.of(customerRole));
        user.setCustomer(savedCustomer);
        userRepository.save(user);

        return savedCustomer;
    }
    
    @Transactional
    public Customer updateApprovedCustomer(Long customerId, CustomerUpdateRequest request) {
        Customer existingCustomer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // Only update the allowed fields
        existingCustomer.setEmail(request.getEmail());
        existingCustomer.setPhone(request.getPhone());
        existingCustomer.setAddress(request.getAddress());
        existingCustomer.setMaritalStatus(request.getMaritalStatus());
        
        // Handle nominee update/creation
        NomineeDTO nomineeDTO = request.getNominee();
        if (nomineeDTO != null && nomineeDTO.getName() != null && !nomineeDTO.getName().isEmpty()) {
            Nominee nominee = existingCustomer.getNominee();
            if (nominee == null) {
                nominee = new Nominee();
            }
            nominee.setName(nomineeDTO.getName());
            nominee.setMobile(nomineeDTO.getMobile());
            nominee.setAddress(nomineeDTO.getAddress());
            nominee.setAadhaarNumber(nomineeDTO.getAadhaarNumber());
            existingCustomer.setNominee(nominee);
        } else {
            // If nomineeDTO is null or name is empty, remove the existing nominee
            existingCustomer.setNominee(null);
        }

        return customerRepository.save(existingCustomer);
    }
    
    public CustomerDTO convertToDto(Customer customer) {
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
        
        // *** THE FIX IS HERE: Set the KYC status on the DTO ***
        if (customer.getKycStatus() != null) {
            dto.setKycStatus(customer.getKycStatus().name());
        }
        
        dto.setRequestedAccountType(customer.getRequestedAccountType());
        dto.setNetBankingEnabled(customer.getNetBankingEnabled());
        dto.setDebitCardIssued(customer.getDebitCardIssued());
        dto.setChequeBookIssued(customer.getChequeBookIssued());

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
    
    @Transactional
    public Customer updateCustomerByAdmin(Long customerId, AdminCustomerUpdateRequest request) {
        Customer existingCustomer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // Update all fields from the admin request
        existingCustomer.setFullName(request.getFullName());
        existingCustomer.setEmail(request.getEmail());
        existingCustomer.setPhone(request.getPhone());
        existingCustomer.setAddress(request.getAddress());
        existingCustomer.setMaritalStatus(request.getMaritalStatus());
        existingCustomer.setProfession(request.getProfession());
        existingCustomer.setFathersName(request.getFathersName());
        
        // Handle nominee update/creation
        NomineeDTO nomineeDTO = request.getNominee();
        if (nomineeDTO != null && nomineeDTO.getName() != null && !nomineeDTO.getName().isEmpty()) {
            Nominee nominee = existingCustomer.getNominee();
            if (nominee == null) {
                nominee = new Nominee();
            }
            nominee.setName(nomineeDTO.getName());
            nominee.setMobile(nomineeDTO.getMobile());
            nominee.setAddress(nomineeDTO.getAddress());
            nominee.setAadhaarNumber(nomineeDTO.getAadhaarNumber());
            existingCustomer.setNominee(nominee);
        } else {
            existingCustomer.setNominee(null);
        }

        return customerRepository.save(existingCustomer);
    }


    
    // --- Other service methods (unchanged) ---
    public Page<Customer> findAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }
    public Page<Customer> searchCustomers(String keyword, Pageable pageable) {
        return customerRepository.searchByKeyword(keyword, pageable);
    }
    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    
    public Optional<Customer> findCustomerByPan(String pan) {
        return customerRepository.findByPan(pan);
    }
    
    public Page<Customer> searchByKeyword(String keyword, Pageable pageable) {
        return customerRepository.searchByKeyword(keyword, pageable);
    }
}