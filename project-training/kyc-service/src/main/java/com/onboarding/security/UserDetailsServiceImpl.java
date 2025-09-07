package com.onboarding.security;

import com.onboarding.model.KycApplication;
import com.onboarding.repository.KycApplicationRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final KycApplicationRepository kycApplicationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(KycApplicationRepository kycApplicationRepository, PasswordEncoder passwordEncoder) {
        this.kycApplicationRepository = kycApplicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // --- Hardcoded admin user for the UI ---
        if ("admin".equalsIgnoreCase(usernameOrEmail)) {
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return new org.springframework.security.core.userdetails.User(
                "admin",
                passwordEncoder.encode("password"), 
                authorities
            );
        }
        
        // *** NEW: Hardcoded internal user for inter-service communication ***
        if ("internal-user".equalsIgnoreCase(usernameOrEmail)) {
             Set<GrantedAuthority> authorities = new HashSet<>();
             authorities.add(new SimpleGrantedAuthority("ROLE_INTERNAL"));
             return new org.springframework.security.core.userdetails.User(
                "internal-user",
                passwordEncoder.encode("internal-password"),
                authorities
             );
        }

        // Find the application by username or email for customer login
        KycApplication application = kycApplicationRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password: " + usernameOrEmail));

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return new org.springframework.security.core.userdetails.User(
            application.getUsername(),
            application.getPassword(),
            authorities
        );
    }
}