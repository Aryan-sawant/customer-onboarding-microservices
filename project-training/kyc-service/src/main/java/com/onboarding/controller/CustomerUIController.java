package com.onboarding.controller;

import com.onboarding.dto.AccountDTO;
import com.onboarding.dto.CustomerDTO;
import com.onboarding.dto.CustomerUpdateRequest;
import com.onboarding.dto.NomineeDTO;
import com.onboarding.feign.AccountClient;
import com.onboarding.feign.CustomerClient;
import com.onboarding.model.KycApplication;
import com.onboarding.repository.KycApplicationRepository;

import java.util.Base64; // <-- Import Base64
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerUIController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerUIController.class);

    private final KycApplicationRepository kycApplicationRepository;
    private final AccountClient accountClient;
    private final CustomerClient customerClient;

    public CustomerUIController(KycApplicationRepository kycApplicationRepository, AccountClient accountClient, CustomerClient custClient) {
        this.kycApplicationRepository = kycApplicationRepository;
        this.accountClient = accountClient;
        this.customerClient = custClient;
    }

    @GetMapping("/dashboard")
    public String showCustomerDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        KycApplication application = kycApplicationRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Could not find application for user: " + username));

        if (application.getCustomerId() != null && "VERIFIED".equals(application.getKycStatus().name())) {
            LOGGER.info("User {} is VERIFIED. Fetching final profile from customer-service.", username);
            try {
                CustomerDTO customer = customerClient.getApprovedCustomerById(application.getCustomerId());
                
                // Merge document info from local record for modal viewing
                addDocumentInfoToDto(customer, application);

                model.addAttribute("customer", customer);
                model.addAttribute("account", accountClient.getAccountByCustomerId(application.getCustomerId()));

            } catch (Exception e) {
                LOGGER.error("Error fetching approved profile for user {}: {}", username, e.getMessage());
                model.addAttribute("accountError", "Could not retrieve your profile details.");
                model.addAttribute("customer", convertToDto(application));
            }
        } else {
            LOGGER.info("User {} is PENDING/REJECTED. Displaying local KYC application data.", username);
            model.addAttribute("customer", convertToDto(application));
        }

        return "customer/dashboard";
    }

    private CustomerDTO convertToDto(KycApplication app) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(app.getId());
        dto.setFullName(app.getFullName());
        dto.setEmail(app.getEmail());
        dto.setPhone(app.getPhone());
        dto.setAddress(app.getAddress());
        dto.setPan(app.getPan());
        dto.setAadhaar(app.getAadhaar());

        if (app.getKycStatus() != null) {
            dto.setKycStatus(app.getKycStatus().name());
        } else {
            dto.setKycStatus("UNKNOWN");
        }

        if (app.getKycNominee() != null) {
            NomineeDTO nomineeDto = new NomineeDTO();
            nomineeDto.setName(app.getKycNominee().getName());
            nomineeDto.setMobile(app.getKycNominee().getMobile());
            dto.setNominee(nomineeDto);
        }
        
        // Add document info for PENDING/REJECTED users
        addDocumentInfoToDto(dto, app);

        return dto;
    }
    
    // Helper method to add document info to any DTO
    private void addDocumentInfoToDto(CustomerDTO dto, KycApplication app) {
        dto.setPassportPhotoBase64(app.getPassportPhotoBase64());
        dto.setPassportPhotoContentType(app.getPassportPhotoContentType());
        dto.setPanPhotoBase64(app.getPanPhotoBase64());
        dto.setPanPhotoContentType(app.getPanPhotoContentType());
        dto.setAadhaarPhotoBase64(app.getAadhaarPhotoBase64());
        dto.setAadhaarPhotoContentType(app.getAadhaarPhotoContentType());
    }

    @GetMapping("/document/{docType}")
    public ResponseEntity<byte[]> viewCustomerDocument(@PathVariable String docType, Authentication authentication) {
        String username = authentication.getName();
        KycApplication application = kycApplicationRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found for user."));

        String base64Data;
        String contentType;

        switch (docType) {
            case "passport":
                base64Data = application.getPassportPhotoBase64();
                contentType = application.getPassportPhotoContentType();
                break;
            case "pan":
                base64Data = application.getPanPhotoBase64();
                contentType = application.getPanPhotoContentType();
                break;
            case "aadhaar":
                base64Data = application.getAadhaarPhotoBase64();
                contentType = application.getAadhaarPhotoContentType();
                break;
            default:
                return ResponseEntity.notFound().build();
        }

        if (base64Data == null || contentType == null) {
            return ResponseEntity.notFound().build();
        }

        // Decode the Base64 String into a byte array
        byte[] documentData = Base64.getDecoder().decode(base64Data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + docType + "\"");

        return new ResponseEntity<>(documentData, headers, HttpStatus.OK);
    }
    
    @GetMapping("/edit-profile")
    public String showEditProfileForm(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        KycApplication app = kycApplicationRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Application not found for user: " + username));
        
        if (!"VERIFIED".equals(app.getKycStatus().name()) || app.getCustomerId() == null) {
            return "redirect:/customer/dashboard";
        }
        
        try {
            CustomerDTO currentCustomerProfile = customerClient.getApprovedCustomerById(app.getCustomerId());
            CustomerUpdateRequest updateRequest = new CustomerUpdateRequest();
            updateRequest.setEmail(currentCustomerProfile.getEmail());
            updateRequest.setPhone(currentCustomerProfile.getPhone());
            updateRequest.setAddress(currentCustomerProfile.getAddress());
            updateRequest.setMaritalStatus(currentCustomerProfile.getMaritalStatus());
            
            if (currentCustomerProfile.getNominee() != null) {
                updateRequest.setNominee(currentCustomerProfile.getNominee());
            } else {
                updateRequest.setNominee(new NomineeDTO());
            }
            
            model.addAttribute("updateRequest", updateRequest);
            return "customer/edit-profile";

        } catch (Exception e) {
            LOGGER.error("Failed to fetch customer profile for editing for user {}: {}", username, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Could not load your profile for editing. Please try again.");
            return "redirect:/customer/dashboard";
        }
    }
    
    @PostMapping("/edit-profile")
    public String processEditProfile(
            @ModelAttribute("updateRequest") CustomerUpdateRequest updateRequest,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        String username = authentication.getName();
        KycApplication app = kycApplicationRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Application not found for user: " + username));

        try {
            customerClient.updateApprovedCustomer(app.getCustomerId(), updateRequest);

            Map<String, Object> accountUpdateData = new HashMap<>();
            if (updateRequest.getNominee() != null && updateRequest.getNominee().getName() != null && !updateRequest.getNominee().getName().isEmpty()) {
                accountUpdateData.put("nomineeRegistered", true);
                accountUpdateData.put("nomineeName", updateRequest.getNominee().getName());
            } else {
                accountUpdateData.put("nomineeRegistered", false);
                accountUpdateData.put("nomineeName", null);
            }
            
            accountClient.updateAccountDetails(app.getCustomerId(), accountUpdateData);

            redirectAttributes.addFlashAttribute("successMessage", "Your profile has been updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "There was an error updating your profile: " + e.getMessage());
        }
        
        return "redirect:/customer/dashboard";
    }
}