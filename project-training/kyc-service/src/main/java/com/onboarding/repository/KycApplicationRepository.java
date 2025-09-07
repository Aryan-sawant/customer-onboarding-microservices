package com.onboarding.repository;

import com.onboarding.model.KycApplication;
import com.onboarding.model.KycStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KycApplicationRepository extends JpaRepository<KycApplication, Long> {

    // Methods for validation during registration
    Optional<KycApplication> findByEmail(String email);
    Optional<KycApplication> findByPan(String pan);
    Optional<KycApplication> findByAadhaar(String aadhaar);
    
    boolean existsByEmailOrUsernameOrPanOrAadhaar(String email, String username, String pan, String aadhaar);

    // Required by CustomerUIController to find the application for the logged-in user.
    Optional<KycApplication> findByUsername(String username);

    // This method is required by UserDetailsServiceImpl for login with either username or email.
    Optional<KycApplication> findByUsernameOrEmail(String username, String email);
    
    @Query("SELECT app FROM KycApplication app LEFT JOIN FETCH app.kycNominee WHERE app.username = :username")
    Optional<KycApplication> findByUsernameWithNominee(@Param("username") String username);
    
    @Query("SELECT app FROM KycApplication app WHERE " +
           "LOWER(app.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(app.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "app.phone LIKE CONCAT('%', :keyword, '%') OR " +
           "app.pan LIKE CONCAT('%', :keyword, '%') OR " +
           "app.aadhaar LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(app.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(app.gender) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(app.maritalStatus) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(app.profession) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(app.requestedAccountType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(CAST(app.kycStatus AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "CAST(app.id AS string) LIKE CONCAT('%', :keyword, '%')")
   Page<KycApplication> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    long countByKycStatus(KycStatus kycStatus);
    
    // *** THE FIX IS APPLIED TO THIS QUERY ***
    // The query now includes a case-insensitive search on the 'fullName' field.
    @Query("SELECT app FROM KycApplication app WHERE " +
            "LOWER(app.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(app.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(app.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "app.pan = :keyword OR " +
            "app.aadhaar = :keyword")
     Optional<KycApplication> findByKeyword(@Param("keyword") String keyword);
    
    List<KycApplication> findTop5ByKycStatusOrderByIdDesc(KycStatus status);
    
    @Query("SELECT app FROM KycApplication app LEFT JOIN FETCH app.kycNominee WHERE " +
            "LOWER(app.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(app.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(app.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "app.pan = :keyword OR " +
            "app.aadhaar = :keyword")
     Optional<KycApplication> findByKeywordWithNominee(@Param("keyword") String keyword);
    
    @Query("SELECT app FROM KycApplication app WHERE app.createdAt BETWEEN :start AND :end")
    List<KycApplication> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}