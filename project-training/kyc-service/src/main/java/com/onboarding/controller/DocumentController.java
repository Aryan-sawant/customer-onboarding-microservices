package com.onboarding.controller;

import com.onboarding.model.KycApplication;
import com.onboarding.repository.KycApplicationRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/applications")
public class DocumentController {

    private final KycApplicationRepository kycApplicationRepository;

    public DocumentController(KycApplicationRepository kycApplicationRepository) {
        this.kycApplicationRepository = kycApplicationRepository;
    }

    /**
     * This is the corrected method for viewing documents.
     * It now correctly uses the separate Base64 data and ContentType fields.
     */
    @GetMapping("/{id}/document/{type}")
    public ResponseEntity<byte[]> getDocument(@PathVariable Long id, @PathVariable String type) {
        
        // 1. Fetch the application from the database
        KycApplication app = kycApplicationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found with ID: " + id));

        String base64Data;
        String contentType;
        
        // 2. Get the correct data and content type from their separate fields
        switch (type.toLowerCase()) {
            case "passport":
                base64Data = app.getPassportPhotoBase64();
                contentType = app.getPassportPhotoContentType();
                break;
            case "pan":
                base64Data = app.getPanPhotoBase64();
                contentType = app.getPanPhotoContentType();
                break;
            case "aadhaar":
                base64Data = app.getAadhaarPhotoBase64();
                contentType = app.getAadhaarPhotoContentType();
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        // 3. Check if either the data or the content type is missing
        if (base64Data == null || base64Data.isEmpty() || contentType == null || contentType.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            // 4. Decode the raw Base64 string into bytes
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            // 5. Build the response with the correct headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(decodedBytes.length);
            headers.add("Content-Disposition", "inline"); // Suggests browser should display the file

            return new ResponseEntity<>(decodedBytes, headers, HttpStatus.OK);
            
        } catch (IllegalArgumentException e) {
            // This will catch errors if the Base64 string is malformed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid Base64 data in database".getBytes());
        }
    }
}