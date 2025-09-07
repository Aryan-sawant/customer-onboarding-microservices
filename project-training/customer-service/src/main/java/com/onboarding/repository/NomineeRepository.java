package com.onboarding.repository;

import com.onboarding.model.Nominee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NomineeRepository extends JpaRepository<Nominee, Long> {

    /**
     * Finds a nominee by their unique Aadhaar number.
     * Useful for validation to ensure one person isn't a nominee for multiple accounts
     * under different names (if that's a business rule).
     */
    Optional<Nominee> findByAadhaarNumber(String aadhaarNumber);
}