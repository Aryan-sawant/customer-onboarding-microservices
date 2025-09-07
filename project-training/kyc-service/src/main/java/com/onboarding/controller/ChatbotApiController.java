package com.onboarding.controller;

import com.onboarding.dto.AccountDTO;
import com.onboarding.dto.ChatbotKycApplicationDTO;
import com.onboarding.dto.CustomerDTO;
import com.onboarding.feign.AccountClient;
import com.onboarding.feign.CustomerClient;
import com.onboarding.model.KycApplication;
import com.onboarding.model.KycStatus;
import com.onboarding.repository.KycApplicationRepository;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotApiController {

    private final KycApplicationRepository kycRepo;
    private final CustomerClient customerClient;
    private final AccountClient accountClient;

    public ChatbotApiController(KycApplicationRepository kycRepo, CustomerClient customerClient, AccountClient accountClient) {
        this.kycRepo = kycRepo;
        this.customerClient = customerClient;
        this.accountClient = accountClient;
    }

    /**
     * The primary "mashup" endpoint. It finds an application/customer and enriches it
     * with account details if they exist.
     */
    @GetMapping("/admin/search-customer")
    public ResponseEntity<?> searchCustomerForAdmin(@RequestParam String keyword) {
        Optional<KycApplication> appOptional = kycRepo.findByKeywordWithNominee(keyword);

        if (appOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        KycApplication app = appOptional.get();
        
        // *** THE FIX: Convert the full entity to the lightweight DTO before returning ***
        ChatbotKycApplicationDTO lightweightAppDTO = ChatbotKycApplicationDTO.fromEntity(app);
        
        Map<String, Object> response = new HashMap<>();
        response.put("kycApplication", lightweightAppDTO); // Send the lightweight object

        // The logic to enrich with customer and account data remains the same
        if (app.getCustomerId() != null) {
            try {
                CustomerDTO customer = customerClient.getCustomerById(app.getCustomerId());
                response.put("customer", customer);
            } catch (Exception e) {}
            try {
                AccountDTO account = accountClient.getAccountByCustomerId(app.getCustomerId());
                response.put("account", account);
            } catch (Exception e) {}
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for dashboard statistics.
     */
    @GetMapping("/admin/dashboard-stats")
    public ResponseEntity<?> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", kycRepo.count());
        stats.put("pending", kycRepo.countByKycStatus(KycStatus.PENDING));
        stats.put("verified", kycRepo.countByKycStatus(KycStatus.VERIFIED));
        stats.put("rejected", kycRepo.countByKycStatus(KycStatus.REJECTED));
        return ResponseEntity.ok(stats);
    }

    /**
     * Endpoint to list applicants by their KYC status.
     */
    @GetMapping("/admin/list-by-kyc")
    public ResponseEntity<?> listCustomersByKyc(@RequestParam String status) {
        try {
            KycStatus kycStatus = KycStatus.valueOf(status.toUpperCase());
            List<KycApplication> applications = kycRepo.findTop5ByKycStatusOrderByIdDesc(kycStatus);
            // Return a simpler list of names and IDs for the chatbot
            return ResponseEntity.ok(applications.stream()
                .map(app -> Map.of("id", app.getId(), "fullName", app.getFullName()))
                .collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid KYC status provided. Use PENDING, VERIFIED, or REJECTED.");
        }
    }
    
    @GetMapping("/admin/accounts-created-on-date")
    public ResponseEntity<List<AccountDTO>> getAccountsByDate(@RequestParam("date") String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);
            
            //Make the actual Feign client call
            List<AccountDTO> accounts = accountClient.getAccountsCreatedBetween(startOfDay, endOfDay);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/admin/applications-created-between")
    public ResponseEntity<List<Map<String, Object>>> getApplicationsByDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        List<KycApplication> applications = kycRepo.findByCreatedAtBetween(start, end);
        
        // Instead of Map.of(), we create a new HashMap for each application.
        // This ensures the type is exactly Map<String, Object>.
        List<Map<String, Object>> result = applications.stream()
                .map(app -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", app.getId());
                    map.put("fullName", app.getFullName());
                    map.put("createdAt", app.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(result);
    }
}