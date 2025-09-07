package com.onboarding.controller;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

import com.onboarding.dto.AccountDTO;
import com.onboarding.dto.AdminCustomerUpdateRequest;
import com.onboarding.dto.CustomerDTO;
import com.onboarding.dto.NomineeDTO;
import com.onboarding.feign.AccountClient;
import com.onboarding.feign.CustomerClient;
import com.onboarding.model.KycApplication; // Import the correct local entity
import com.onboarding.model.KycStatus;
import com.onboarding.repository.KycApplicationRepository; // Import the local repository
import com.onboarding.service.KycProcessingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Dependency is now the local repository, not the Feign client
    private final KycApplicationRepository kycApplicationRepository;
    private final KycProcessingService kycProcessingService;
    private final CustomerClient customerClient;
    private final AccountClient accountClient; 
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    public AdminController(KycApplicationRepository kycApplicationRepository, KycProcessingService kycProcessingService,CustomerClient customerClient,AccountClient accountClient) {
        this.kycApplicationRepository = kycApplicationRepository;
        this.kycProcessingService = kycProcessingService;
        this.accountClient=accountClient;
        this.customerClient=customerClient;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String keyword) {
        
        boolean isSearchActive = StringUtils.hasText(keyword);
        model.addAttribute("searchActive", isSearchActive);
        model.addAttribute("keyword", keyword);
        
        long totalApplications = kycApplicationRepository.count();
        long pendingCount = kycApplicationRepository.countByKycStatus(KycStatus.PENDING);
        // Verified count is the total minus pending and rejected
        long rejectedCount = kycApplicationRepository.countByKycStatus(KycStatus.REJECTED);
        long verifiedCount = totalApplications - pendingCount - rejectedCount;

        model.addAttribute("totalApplications", totalApplications);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("verifiedCount", verifiedCount);

        if (isSearchActive) {
            // --- SEARCH LOGIC ---
            LOGGER.info("Admin search activated with keyword: {}", keyword);
            
            // 1. Search local PENDING/REJECTED applications
            Page<KycApplication> localResults = kycApplicationRepository.searchByKeyword(keyword, Pageable.unpaged());
            
            // 2. Search remote VERIFIED customers
            List<CustomerDTO> remoteResults = customerClient.searchCustomers(keyword);
            
            // 3. Combine results into a single list for the view
            List<Object> combinedResults = new ArrayList<>();
            combinedResults.addAll(localResults.getContent());
            combinedResults.addAll(remoteResults);
            
            model.addAttribute("searchResults", combinedResults);
            
        } else {
            // --- DEFAULT PAGINATED VIEW LOGIC ---
            Pageable pageable = PageRequest.of(page, size);
            Page<KycApplication> applicationPage = kycApplicationRepository.findAll(pageable);
            model.addAttribute("applications", applicationPage);
        }
        
        
        
        return "admin/dashboard";
    }

    @GetMapping("/customer/{id}")
    public String customerDetails(@PathVariable Long id, Model model) {
        KycApplication application = kycApplicationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid application Id:" + id));
        
        if ("VERIFIED".equals(application.getKycStatus().name()) && application.getCustomerId() != null) {
            LOGGER.info("Admin is viewing a VERIFIED profile. Fetching live data for customer ID: {}", application.getCustomerId());
            try {
                CustomerDTO customer = customerClient.getCustomerById(application.getCustomerId());
                model.addAttribute("customer", customer);

                AccountDTO account = accountClient.getAccountByCustomerId(application.getCustomerId());
                model.addAttribute("account", account);
                
            } catch (Exception e) {
                LOGGER.error("Could not fetch downstream data for verified customer ID {}: {}", application.getCustomerId(), e.getMessage());
                model.addAttribute("errorMessage", "Could not load complete profile. Downstream services may be unavailable.");
                model.addAttribute("customer", application);
            }
        } else {
            LOGGER.info("Admin is viewing a PENDING/REJECTED application. Using local data for application ID: {}", id);
            model.addAttribute("customer", application);
        }
        
        return "admin/customer-details"; // This now requires a new unified template
    }

    @PostMapping("/customer/{id}/process")
    public String processKyc(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String rejectionReason,
            RedirectAttributes redirectAttributes) {
        try {
            // The processKyc method in the service already knows it's dealing with an application ID
            kycProcessingService.processKyc(id, approved, rejectionReason);
            String status = approved ? "approved" : "rejected";
            redirectAttributes.addFlashAttribute("message", "Application " + id + " has been successfully " + status + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing application: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/customer/{id}/edit")
    public String showAdminEditForm(@PathVariable Long id, Model model) {
        try {
            CustomerDTO customer = customerClient.getCustomerById(id);
            AdminCustomerUpdateRequest updateRequest = new AdminCustomerUpdateRequest();
            // Map data from the fetched customer DTO to the update request DTO
            updateRequest.setFullName(customer.getFullName());
            updateRequest.setEmail(customer.getEmail());
            updateRequest.setPhone(customer.getPhone());
            updateRequest.setAddress(customer.getAddress());
            updateRequest.setMaritalStatus(customer.getMaritalStatus());
            updateRequest.setProfession(customer.getProfession());
            updateRequest.setFathersName(customer.getFathersName());
            
            // Handle pre-population of nominee form
            if (customer.getNominee() != null) {
                updateRequest.setNominee(customer.getNominee());
            } else {
                updateRequest.setNominee(new NomineeDTO());
            }
            
            model.addAttribute("updateRequest", updateRequest);
            model.addAttribute("customerId", id);
            return "admin/edit-customer";
        } catch (Exception e) {
            return "redirect:/admin/dashboard";
        }
    }
    
    /**
     * *** NEW METHOD: Processes the admin's edit form submission ***
     */
    @PostMapping("/customer/{id}/edit")
    public String processAdminEditForm(@PathVariable Long id, 
                                     @ModelAttribute("updateRequest") AdminCustomerUpdateRequest updateRequest, 
                                     RedirectAttributes redirectAttributes) {
        try {
            customerClient.updateCustomerByAdmin(id, updateRequest);
            redirectAttributes.addFlashAttribute("message", "Customer " + id + " updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating customer: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    /**
     * *** NEW METHOD: Handles the account deactivation request ***
     */
 // In AdminController.java
    @PostMapping("/customer/{id}/deactivate")
    public String deactivateAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // We need the customerId, not the kyc application id.
            // We must first fetch the application to get the linked customerId.
            KycApplication app = kycApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
            
            if(app.getCustomerId() == null) {
                throw new IllegalStateException("Cannot deactivate account for a customer that has not been approved yet.");
            }
            
            accountClient.deactivateAccount(app.getCustomerId());
            redirectAttributes.addFlashAttribute("message", "Account for customer " + app.getCustomerId() + " has been deactivated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deactivating account: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }
}