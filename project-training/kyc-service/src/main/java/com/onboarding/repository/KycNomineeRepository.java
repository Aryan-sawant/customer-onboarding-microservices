package com.onboarding.repository;

import com.onboarding.model.KycNominee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KycNomineeRepository extends JpaRepository<KycNominee, Long> {
    Optional<KycNominee> findByAadhaarNumber(String aadhaarNumber);
}