package com.onboarding; // Or your package name in customer-service

import com.onboarding.model.Role;
import com.onboarding.model.User;
import com.onboarding.repository.RoleRepository;
import com.onboarding.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("Customer-Service DataLoader is running. Checking for default roles and users...");

        // Create Roles if they don't exist
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN");
        Role customerRole = createRoleIfNotFound("ROLE_CUSTOMER");
        Role internalRole = createRoleIfNotFound("ROLE_INTERNAL");

        // Create Admin User if it doesn't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("password")); // Use a secure password
            adminUser.setRoles(Set.of(adminRole));
            userRepository.save(adminUser);
            LOGGER.info("Created default ADMIN user: 'admin'");
        }

        // Create Internal API User for Chatbot if it doesn't exist
        if (userRepository.findByUsername("internal-user").isEmpty()) {
            User internalUser = new User();
            internalUser.setUsername("internal-user");
            internalUser.setPassword(passwordEncoder.encode("internal-password")); // Use a secure password
            internalUser.setRoles(Set.of(internalRole));
            userRepository.save(internalUser);
            LOGGER.info("Created default INTERNAL API user: 'internal-user'");
        }
        
        LOGGER.info("DataLoader finished.");
    }

    private Role createRoleIfNotFound(String name) {
        return roleRepository.findByName(name)
            .orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName(name);
                LOGGER.info("Created new role: {}", name);
                return roleRepository.save(newRole);
            });
    }
}
