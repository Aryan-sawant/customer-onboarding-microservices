package com.onboarding.repository;

import com.onboarding.model.Customer;
import com.onboarding.model.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	@Query("SELECT c FROM Customer c WHERE " +
	           "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "c.phone LIKE CONCAT('%', :keyword, '%') OR " +
	           "c.pan LIKE CONCAT('%', :keyword, '%') OR " +
	           "c.aadhaar LIKE CONCAT('%', :keyword, '%') OR " +
	           "LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "LOWER(c.gender) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "LOWER(c.maritalStatus) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "LOWER(c.profession) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "LOWER(c.requestedAccountType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
	           "CAST(c.id AS string) LIKE CONCAT('%', :keyword, '%')")
	    Page<Customer> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);


    // --- Methods for duplicate checks during registration ---
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPan(String pan);
    Optional<Customer> findByAadhaar(String aadhaar);

    // --- Methods for dashboard stats ---
    long countByKycStatus(KycStatus kycStatus);
    List<Customer> findTop5ByKycStatusOrderByIdDesc(KycStatus kycStatus);
    
    
    
    // --- Method for internal API (no longer needed, but we can keep it for now) ---
    // Optional<Customer> findByKycApplicationId(Long kycId);
    
    // The old search method is now replaced by the more powerful `searchByKeyword`
    // Page<Customer> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullName, String email, Pageable pageable);
}