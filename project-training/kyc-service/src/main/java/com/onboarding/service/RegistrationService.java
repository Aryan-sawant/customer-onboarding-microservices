package com.onboarding.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.onboarding.dto.FullRegistrationRequest;
import com.onboarding.dto.NewKycApplicationEvent;
import com.onboarding.dto.NomineeDTO;
import com.onboarding.exception.CustomerAlreadyExistsException;
import com.onboarding.model.KycApplication;
import com.onboarding.model.KycNominee;
import com.onboarding.model.KycStatus;
import com.onboarding.repository.KycApplicationRepository;
import com.onboarding.repository.KycNomineeRepository;

@Service
public class RegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);

    private final KycApplicationRepository kycRepo;
    private final KycNomineeRepository kycNomineeRepo;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;

    public RegistrationService(KycApplicationRepository kycRepo, KycNomineeRepository kycNomineeRepo, PasswordEncoder passwordEncoder, KafkaProducerService kafkaProducerService) {
        this.kycRepo = kycRepo;
        this.kycNomineeRepo = kycNomineeRepo;
        this.passwordEncoder = passwordEncoder;
        this.kafkaProducerService = kafkaProducerService;
    }

    @Transactional
    public void processRegistration(FullRegistrationRequest request, MultipartFile passportPhoto, MultipartFile panDoc, MultipartFile aadhaarDoc) throws IOException {
        validateUniqueness(request);

        KycApplication application = new KycApplication();
        mapRequestToApplication(application, request);
        processDocuments(application, passportPhoto, panDoc, aadhaarDoc);
        application.setPassword(passwordEncoder.encode(request.getPassword()));
        
        if (request.getNominee() != null && request.getNominee().getName() != null && !request.getNominee().getName().isEmpty()) {
            handleNominee(application, request.getNominee());
        }

        KycApplication savedApplication = kycRepo.save(application);
        
        LOGGER.info("New KYC application {} submitted for user {}.", savedApplication.getId(), savedApplication.getUsername());
        NewKycApplicationEvent event = new NewKycApplicationEvent(
            savedApplication.getId(),
            savedApplication.getFullName(),
            savedApplication.getEmail()
        );
        kafkaProducerService.sendNewKycApplicationNotification(event);
    }

    @Transactional
    public void updateApplication(KycApplication appFromSession, MultipartFile passportPhoto, MultipartFile panDoc, MultipartFile aadhaarDoc) throws IOException {
        KycApplication originalApp = kycRepo.findById(appFromSession.getId())
            .orElseThrow(() -> new RuntimeException("Application not found for update: " + appFromSession.getId()));

        // Map all the fields from the session object to the persisted entity
        originalApp.setFullName(appFromSession.getFullName());
        originalApp.setPhone(appFromSession.getPhone());
        originalApp.setGender(appFromSession.getGender());
        originalApp.setMaritalStatus(appFromSession.getMaritalStatus());
        originalApp.setFathersName(appFromSession.getFathersName());
        originalApp.setNationality(appFromSession.getNationality());
        originalApp.setProfession(appFromSession.getProfession());
        originalApp.setAddress(appFromSession.getAddress());
        originalApp.setRequestedAccountType(appFromSession.getRequestedAccountType());
        originalApp.setNetBankingEnabled(appFromSession.getNetBankingEnabled());
        originalApp.setDebitCardIssued(appFromSession.getDebitCardIssued());
        originalApp.setChequeBookIssued(appFromSession.getChequeBookIssued());

        KycNominee nomineeFromSession = appFromSession.getKycNominee();
        if (nomineeFromSession != null && nomineeFromSession.getName() != null && !nomineeFromSession.getName().isEmpty()) {
            KycNominee nomineeToSave = originalApp.getKycNominee();
            if (nomineeToSave == null) {
                nomineeToSave = new KycNominee();
            }
            nomineeToSave.setName(nomineeFromSession.getName());
            nomineeToSave.setMobile(nomineeFromSession.getMobile());
            nomineeToSave.setAddress(nomineeFromSession.getAddress());
            nomineeToSave.setAadhaarNumber(nomineeFromSession.getAadhaarNumber());
            originalApp.setKycNominee(nomineeToSave);
        } else {
            originalApp.setKycNominee(null);
        }

        // Process any newly uploaded files (this will now also save content types)
        processDocuments(originalApp, passportPhoto, panDoc, aadhaarDoc);
        
        boolean wasRejected = originalApp.getKycStatus() == KycStatus.REJECTED;

        if (wasRejected) {
            originalApp.setKycStatus(KycStatus.PENDING);
            LOGGER.info("KYC application {} status reset to PENDING after re-application.", originalApp.getId());
        }
        
        KycApplication savedApplication = kycRepo.save(originalApp);

        if (wasRejected) {
            LOGGER.info("Sending re-application notification for KYC application {}.", savedApplication.getId());
            NewKycApplicationEvent event = new NewKycApplicationEvent(
                savedApplication.getId(),
                savedApplication.getFullName(),
                savedApplication.getEmail(),
                true 
            );
            kafkaProducerService.sendNewKycApplicationNotification(event);
        }
    }

    private void validateUniqueness(FullRegistrationRequest request) {
        if (kycRepo.existsByEmailOrUsernameOrPanOrAadhaar(request.getEmail(), request.getUsername(), request.getPan(), request.getAadhaar())) {
            throw new CustomerAlreadyExistsException("A user with the same Email, Username, PAN, or Aadhaar already exists.");
        }
    }

    private void mapRequestToApplication(KycApplication app, FullRegistrationRequest req) {
        // This helper method correctly maps fields.
        app.setFullName(req.getFullname()); // Corrected from getFullname() to getFullName() if DTO has standard getter
        app.setEmail(req.getEmail());
        app.setPhone(req.getPhone());
        app.setDob(req.getDob());
        app.setAddress(req.getAddress());
        app.setGender(req.getGender());
        app.setMaritalStatus(req.getMaritalStatus());
        app.setFathersName(req.getFathersName());
        app.setNationality(req.getNationality());
        app.setProfession(req.getProfession());
        app.setPan(req.getPan());
        app.setAadhaar(req.getAadhaar());
        app.setUsername(req.getUsername());
        app.setRequestedAccountType(req.getRequestedAccountType());
        app.setNetBankingEnabled(req.isNetBankingEnabled());
        app.setDebitCardIssued(req.isDebitCardIssued());
        app.setChequeBookIssued(req.isChequeBookIssued());
    }
    
    private void handleNominee(KycApplication application, NomineeDTO nomineeDTO) {
        KycNominee nominee = new KycNominee();
        nominee.setName(nomineeDTO.getName());
        nominee.setMobile(nomineeDTO.getMobile());
        nominee.setAddress(nomineeDTO.getAddress());
        nominee.setAadhaarNumber(nomineeDTO.getAadhaarNumber());
        application.setKycNominee(nominee);
    }

    private void processDocuments(KycApplication app, MultipartFile passport, MultipartFile pan, MultipartFile aadhaar) throws IOException {
        // --- THE FIX IS APPLIED HERE ---
        if (passport != null && !passport.isEmpty()) {
            app.setPassportPhotoContentType(passport.getContentType());
            app.setPassportPhotoBase64(Base64.getEncoder().encodeToString(passport.getBytes()));
        }
        if (pan != null && !pan.isEmpty()) {
            app.setPanPhotoContentType(pan.getContentType());
            app.setPanPhotoBase64(Base64.getEncoder().encodeToString(pan.getBytes()));
        }
        if (aadhaar != null && !aadhaar.isEmpty()) {
            app.setAadhaarPhotoContentType(aadhaar.getContentType());
            app.setAadhaarPhotoBase64(Base64.getEncoder().encodeToString(aadhaar.getBytes()));
        }
    }
}