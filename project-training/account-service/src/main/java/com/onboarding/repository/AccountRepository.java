package com.onboarding.repository;

import com.onboarding.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCustomerId(Long customerId);
    List<Account> findByCustomerIdIn(List<Long> customerIds);
    List<Account> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Optional<Account> findByKycApplicationId(Long kycApplicationId);
}