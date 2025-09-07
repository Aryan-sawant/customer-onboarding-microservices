package com.onboarding.repository;

import com.onboarding.model.Customer; // <-- Make sure this import exists
import com.onboarding.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user directly by their unique username.
     * Used for login and validation.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their associated customer's email address.
     * Used as an alternative login method.
     */
    Optional<User> findByCustomer_Email(String email);

    /**
     * Finds a user by the associated Customer entity object.
     * This is the most reliable way to find the user linked to a specific customer,
     * which is crucial for the delete operation.
     */
    Optional<User> findByCustomer(Customer customer); // <-- THIS IS THE NEW, REQUIRED METHOD
    Optional<User> findByCustomerId(Long customerId);

}