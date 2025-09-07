package com.onboarding.service;

import com.onboarding.dto.*;
import com.onboarding.feign.AccountClient;
import com.onboarding.feign.CustomerClient;
import com.onboarding.model.KycApplication;
import com.onboarding.model.KycNominee;
import com.onboarding.model.KycStatus;
import com.onboarding.repository.KycApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
public class KycProcessingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KycProcessingService.class);

    private final KycApplicationRepository kycRepo;
    private final KafkaProducerService kafkaProducerService;
    private final AccountClient accountClient;
    private final CustomerClient customerClient;

    public KycProcessingService(KycApplicationRepository kycRepo, KafkaProducerService kafka, AccountClient accClient, CustomerClient custClient) {
        this.kycRepo = kycRepo;
        this.kafkaProducerService = kafka;
        this.accountClient = accClient;
        this.customerClient = custClient;
    }

    @Transactional
    public void processKyc(Long applicationId, boolean isApproved, String rejectionReason) {
        KycApplication application = kycRepo.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("KYC Application not found with ID: " + applicationId));

        if (application.getKycStatus() != KycStatus.PENDING) {
            throw new IllegalStateException("This application has already been processed.");
        }

        KycStatusUpdateEvent emailEvent = new KycStatusUpdateEvent();
        emailEvent.setCustomerName(application.getFullName());
        emailEvent.setCustomerEmail(application.getEmail());

        if (isApproved) {
            LOGGER.info("Approving KYC for application ID {}.", applicationId);
            application.setKycStatus(KycStatus.VERIFIED);
            
            CustomerCreationResponseDTO newCustomer = createApprovedCustomerInCustomerService(application);
            application.setCustomerId(newCustomer.getId());
            
            createInactiveAccountInAccountService(newCustomer.getId(), application);
            AccountDTO activeAccount = accountClient.activateAccount(newCustomer.getId());

            emailEvent.setKycStatus("VERIFIED");
            emailEvent.setAccountNumber(activeAccount.getAccountNumber());
            emailEvent.setAccountType(activeAccount.getAccountType());
            emailEvent.setIfscCode(activeAccount.getIfscCode());
        } else {
            LOGGER.warn("Rejecting KYC for application ID {} with reason: {}", applicationId, rejectionReason);
            application.setKycStatus(KycStatus.REJECTED);
            emailEvent.setKycStatus("REJECTED");
            emailEvent.setRejectionReason(rejectionReason);
        }
        
        kycRepo.save(application);
        kafkaProducerService.sendKycUpdateNotification(emailEvent);
    }

    private CustomerCreationResponseDTO createApprovedCustomerInCustomerService(KycApplication app) {
        KycApplicationDataDTO kycData = new KycApplicationDataDTO();
        
        kycData.setId(app.getId());
        kycData.setFullName(app.getFullName());
        kycData.setDob(app.getDob());
        kycData.setGender(app.getGender());
        kycData.setMaritalStatus(app.getMaritalStatus());
        kycData.setFathersName(app.getFathersName());
        kycData.setNationality(app.getNationality());
        kycData.setProfession(app.getProfession());
        kycData.setAddress(app.getAddress());
        kycData.setEmail(app.getEmail());
        kycData.setPhone(app.getPhone());
        kycData.setPan(app.getPan());
        kycData.setAadhaar(app.getAadhaar());
        kycData.setUsername(app.getUsername());
        kycData.setPassword(app.getPassword());
        kycData.setRequestedAccountType(app.getRequestedAccountType());
        kycData.setNetBankingEnabled(app.getNetBankingEnabled());
        kycData.setDebitCardIssued(app.getDebitCardIssued());
        kycData.setChequeBookIssued(app.getChequeBookIssued());
        
        if (app.getPassportPhotoBase64() != null) { kycData.setPassportPhotoBase64(extractBase64Data(app.getPassportPhotoBase64())); }
        if (app.getPanPhotoBase64() != null) {
            kycData.setPanPhotoBase64(extractBase64Data(app.getPanPhotoBase64()));
            kycData.setPanPhotoContentType(extractMimeType(app.getPanPhotoBase64()));
        }
        if (app.getAadhaarPhotoBase64() != null) {
            kycData.setAadhaarPhotoBase64(extractBase64Data(app.getAadhaarPhotoBase64()));
            kycData.setAadhaarPhotoContentType(extractMimeType(app.getAadhaarPhotoBase64()));
        }
        
        if (app.getKycNominee() != null) {
            KycNominee kycNominee = app.getKycNominee();
            NomineeDTO nomineeDTO = new NomineeDTO();
            nomineeDTO.setName(kycNominee.getName());
            nomineeDTO.setMobile(kycNominee.getMobile());
            nomineeDTO.setAddress(kycNominee.getAddress());
            nomineeDTO.setAadhaarNumber(kycNominee.getAadhaarNumber());
            kycData.setNominee(nomineeDTO);
        }
        
        return customerClient.createApprovedCustomer(kycData);
    }
    
    private void createInactiveAccountInAccountService(Long customerId, KycApplication app) {
        Map<String, Object> creationData = new HashMap<>();
        creationData.put("customerId", customerId);
        creationData.put("kycApplicationId", app.getId());
        creationData.put("accountType", app.getRequestedAccountType());
        creationData.put("netBankingEnabled", app.getNetBankingEnabled());
        creationData.put("debitCardIssued", app.getDebitCardIssued());
        creationData.put("chequeBookIssued", app.getChequeBookIssued());
        
        if (app.getKycNominee() != null) {
            creationData.put("nomineeRegistered", true);
            creationData.put("nomineeName", app.getKycNominee().getName());
        } else {
            creationData.put("nomineeRegistered", false);
            creationData.put("nomineeName", null);
        }

        accountClient.createInactiveAccount(creationData);
    }
    
    private String extractBase64Data(String dataUri) {
        if (dataUri == null || !dataUri.contains(",")) return null;
        return dataUri.split(",")[1];
    }
    
    private String extractMimeType(String dataUri) {
        if (dataUri == null || !dataUri.contains(",")) return null;
        return dataUri.split(",")[0].split(":")[1].split(";")[0];
    }
}