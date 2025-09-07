package com.onboarding.security;

import com.onboarding.model.User;
import com.onboarding.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginIdentifier) throws UsernameNotFoundException {
        // --- THE NEW, SMARTER LOGIN LOGIC ---

        // Step 1: Try to find the user by their direct username first.
        // This will find the 'admin' user and any customer using their username.
        Optional<User> userOptional = userRepository.findByUsername(loginIdentifier);

        // Step 2: If not found by username, try finding them by their associated customer's email.
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByCustomer_Email(loginIdentifier);
        }

        // Step 3: If still not found after both attempts, the user does not exist.
        User user = userOptional.orElseThrow(() ->
            new UsernameNotFoundException("No user found with username or email: " + loginIdentifier)
        );

        // The rest of the logic is the same
        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }
}