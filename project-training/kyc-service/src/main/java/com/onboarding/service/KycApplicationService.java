package com.onboarding.service;

import com.onboarding.dto.NewKycApplicationEvent;
import com.onboarding.exception.CustomerAlreadyExistsException;
import com.onboarding.model.KycApplication;
import com.onboarding.model.KycStatus;
import com.onboarding.repository.KycApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class KycApplicationService {

    private final KycApplicationRepository kycApplicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;

    public KycApplicationService(KycApplicationRepository kycRepo, PasswordEncoder encoder, KafkaProducerService kafka) {
        this.kycApplicationRepository = kycRepo;
        this.passwordEncoder = encoder;
        this.kafkaProducerService = kafka;
    }

    @Transactional
    public KycApplication submitApplication(KycApplication application) {
        // --- Validation ---
        kycApplicationRepository.findByUsername(application.getUsername()).ifPresent(u -> {
            throw new CustomerAlreadyExistsException("Username '" + application.getUsername() + "' is already taken.");
        });
        kycApplicationRepository.findByEmail(application.getEmail()).ifPresent(u -> {
            throw new CustomerAlreadyExistsException("Email '" + application.getEmail() + "' is already registered.");
        });
        kycApplicationRepository.findByPan(application.getPan()).ifPresent(u -> {
            throw new CustomerAlreadyExistsException("PAN '" + application.getPan() + "' is already registered.");
        });
        kycApplicationRepository.findByAadhaar(application.getAadhaar()).ifPresent(u -> {
            throw new CustomerAlreadyExistsException("Aadhaar '" + application.getAadhaar() + "' is already registered.");
        });

        // Encrypt the password before saving
        application.setPassword(passwordEncoder.encode(application.getPassword()));
        
        KycApplication savedApplication = kycApplicationRepository.save(application);
        
        // Notify admin about the new application via Kafka
        NewKycApplicationEvent event = new NewKycApplicationEvent(
            savedApplication.getId(),
            savedApplication.getFullName(),
            savedApplication.getEmail()
        );
        kafkaProducerService.sendNewKycApplicationNotification(event);

        return savedApplication;
    }

    // --- Methods for the Admin Dashboard ---
    public Page<KycApplication> findAllApplications(Pageable pageable) {
        return kycApplicationRepository.findAll(pageable);
    }

    public Optional<KycApplication> findApplicationById(Long id) {
        return kycApplicationRepository.findById(id);
    }
    
    public long countByKycStatus(KycStatus status) {
        // You would add a countByKycStatus method to your KycApplicationRepository
        // For now, we'll placeholder it.
        return kycApplicationRepository.count(); // This is a simplification
    }
}