package com.onboarding.controller;

import com.onboarding.dto.FullRegistrationRequest; // We'll create this DTO in the kyc-service
import com.onboarding.dto.NomineeDTO;
import com.onboarding.service.RegistrationService; // We'll create this service
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ui/register")
@SessionAttributes("registrationRequest") // Manages the form data across the session
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // Initializes a fresh object for the session when the flow starts
    @ModelAttribute("registrationRequest")
    public FullRegistrationRequest getFullRegistrationRequest() {
        return new FullRegistrationRequest();
    }

    // STEP 1: Show Personal Info Page
    @GetMapping
    public String showStep1_PersonalInfo(Model model, @ModelAttribute("registrationRequest") FullRegistrationRequest request) {
        model.addAttribute("registrationRequest", request);
        return "ui/register/step1-personal";
    }

    @PostMapping("/step1")
    public String processStep1_PersonalInfo(@Valid @ModelAttribute("registrationRequest") FullRegistrationRequest request, BindingResult result) {
        if (result.hasFieldErrors("fullname") || result.hasFieldErrors("email") || result.hasFieldErrors("phone") ||
            result.hasFieldErrors("dob") || result.hasFieldErrors("gender") || result.hasFieldErrors("maritalStatus") ||
            result.hasFieldErrors("fathersName") || result.hasFieldErrors("nationality") || result.hasFieldErrors("profession") ||
            result.hasFieldErrors("address") || result.hasFieldErrors("pan") || result.hasFieldErrors("aadhaar")) {
            return "ui/register/step1-personal"; // Stay on page 1 if errors exist
        }
        return "redirect:/ui/register/nominee"; // Proceed to step 2
    }

    // STEP 2: Show Nominee Page
    @GetMapping("/nominee")
    public String showStep2_Nominee(Model model, @ModelAttribute("registrationRequest") FullRegistrationRequest request) {
        if (request.getNominee() == null) {
            request.setNominee(new NomineeDTO());
        }
        model.addAttribute("registrationRequest", request);
        return "ui/register/step2-nominee";
    }
    
    @PostMapping("/step2")
    public String processStep2_Nominee(@ModelAttribute("registrationRequest") FullRegistrationRequest request, @RequestParam(value = "addNominee", required = false) String addNominee) {
        if (addNominee == null) { // User chose 'Skip'
            request.setNominee(null);
        }
        // Validation for nominee will happen on the final submission
        return "redirect:/ui/register/services"; // Proceed to step 3
    }
    
    // STEP 3: Show Services Page
    @GetMapping("/services")
    public String showStep3_Services(Model model, @ModelAttribute("registrationRequest") FullRegistrationRequest request) {
        model.addAttribute("registrationRequest", request);
        return "ui/register/step3-services";
    }

    @PostMapping("/step3")
    public String processStep3_Services(
            @ModelAttribute("registrationRequest") FullRegistrationRequest request,
            @RequestParam(value = "netBankingEnabled", required = false) String netBankingEnabled,
            @RequestParam(value = "debitCardIssued", required = false) String debitCardIssued,
            @RequestParam(value = "chequeBookIssued", required = false) String chequeBookIssued) {
        
        // If the checkbox was checked, the string value will be "on". If not, it will be null.
        request.setNetBankingEnabled(netBankingEnabled != null);
        request.setDebitCardIssued(debitCardIssued != null);
        request.setChequeBookIssued(chequeBookIssued != null);

        return "redirect:/ui/register/documents"; // Proceed to step 4
    }

    // STEP 4: Show Documents Upload Page
    @GetMapping("/documents")
    public String showStep4_Documents(Model model, @ModelAttribute("registrationRequest") FullRegistrationRequest request) {
        model.addAttribute("registrationRequest", request);
        return "ui/register/step4-documents";
    }
    
    // STEP 5: Show Final Review & Credentials Page
    @GetMapping("/finish")
    public String showStep5_Finish(Model model, @ModelAttribute("registrationRequest") FullRegistrationRequest request) {
        model.addAttribute("registrationRequest", request);
        return "ui/register/step5-finish";
    }

    // FINAL SUBMISSION
    @PostMapping("/submit")
    public String processFinalSubmission(
            @Valid @ModelAttribute("registrationRequest") FullRegistrationRequest request,
            BindingResult result,
            @RequestParam("passportPhoto") MultipartFile passportPhoto,
            @RequestParam("panDoc") MultipartFile panDoc,
            @RequestParam("aadhaarDoc") MultipartFile aadhaarDoc,
            SessionStatus sessionStatus,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            // If there are validation errors on the final page (username/password), stay there
            return "ui/register/step5-finish";
        }
        
        try {
            registrationService.processRegistration(request, passportPhoto, panDoc, aadhaarDoc);
            sessionStatus.setComplete(); // Clear the session attribute
            redirectAttributes.addFlashAttribute("successMessage", "Registration submitted successfully! You can now log in to check your application status.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Submission failed: " + e.getMessage());
            return "redirect:/ui/register"; // On failure, restart the process
        }
    }
}