package com.onboarding;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.onboarding.model.KycApplication;
import com.onboarding.repository.KycApplicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final KycApplicationRepository kycApplicationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(KycApplicationRepository kycApplicationRepository, PasswordEncoder passwordEncoder) {
        this.kycApplicationRepository = kycApplicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Since the User table is now in another service, we can't create a real User entity here.
        // The UserDetailsServiceImpl is hardcoded to handle the 'admin' login.
        // This DataLoader's main purpose in a real app would be to seed other data if needed.
        // For our case, we can simply log that the admin user is conceptually available.
        LOGGER.info("KYC-Service DataLoader is running.");
        LOGGER.info("The 'admin' user is configured in UserDetailsServiceImpl for login.");
        LOGGER.info("To log in, use username='admin' and password='password'.");
    }
}