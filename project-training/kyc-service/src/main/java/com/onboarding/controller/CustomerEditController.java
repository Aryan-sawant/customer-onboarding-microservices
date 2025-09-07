package com.onboarding.controller;

import com.onboarding.model.KycApplication;
import com.onboarding.model.KycNominee;
import com.onboarding.model.KycStatus;
import com.onboarding.repository.KycApplicationRepository;
import com.onboarding.service.RegistrationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer/edit")
@SessionAttributes("editableApplication") // We will only manage ONE object in the session
public class CustomerEditController {

    private final KycApplicationRepository kycRepo;
    private final RegistrationService registrationService;

    public CustomerEditController(KycApplicationRepository kycRepo, RegistrationService registrationService) {
        this.kycRepo = kycRepo;
        this.registrationService = registrationService;
    }

    @ModelAttribute("editableApplication")
    public KycApplication getEditableApplication(Authentication authentication) {
        String username = authentication.getName();
        
        // *** THE FIX: Call the new repository method that uses JOIN FETCH ***
        return kycRepo.findByUsernameWithNominee(username)
            .orElseThrow(() -> new RuntimeException("Application not found for user: " + username));
    }

    @GetMapping
    public String showEditStep1_PersonalInfo(Model model, @ModelAttribute("editableApplication") KycApplication app) {
        if (app.getKycStatus() == KycStatus.VERIFIED) {
            return "redirect:/customer/dashboard";
        }
        model.addAttribute("editableApplication", app);
        return "customer/edit-step1-personal";
    }

    @PostMapping("/step1")
    public String processEditStep1_PersonalInfo(@ModelAttribute("editableApplication") KycApplication app, BindingResult result) {
        if (result.hasErrors()) {
            return "customer/edit-step1-personal";
        }
        return "redirect:/customer/edit/nominee";
    }
    
    @GetMapping("/nominee")
    public String showEditStep2_Nominee(Model model, @ModelAttribute("editableApplication") KycApplication app) {
        // If the app doesn't have a nominee yet, create a new empty one for the form
        if (app.getKycNominee() == null) {
            app.setKycNominee(new KycNominee());
        }
        model.addAttribute("editableApplication", app);
        return "customer/edit-step2-nominee";
    }
    
    @PostMapping("/step2")
    public String processEditStep2_Nominee(@ModelAttribute("editableApplication") KycApplication app, @RequestParam(value = "addNominee", required = false) String addNominee) {
        if (addNominee == null) {
            // If the user clicks "Remove/Skip", we nullify the nominee object.
            app.setKycNominee(null);
        }
        // Otherwise, the nominee details from the form are already bound to app.getKycNominee().
        // We just proceed to the next step.
        return "redirect:/customer/edit/services";
    }


    @GetMapping("/services")
    public String showEditStep3_Services(Model model, @ModelAttribute("editableApplication") KycApplication app) {
        model.addAttribute("editableApplication", app);
        return "customer/edit-step3-services";
    }

    @PostMapping("/step3")
    public String processEditStep3_Services(
            @ModelAttribute("editableApplication") KycApplication app,
            @RequestParam(value = "netBankingEnabled", required = false) String netBankingEnabled,
            @RequestParam(value = "debitCardIssued", required = false) String debitCardIssued,
            @RequestParam(value = "chequeBookIssued", required = false) String chequeBookIssued) {
        
        app.setNetBankingEnabled(netBankingEnabled != null);
        app.setDebitCardIssued(debitCardIssued != null);
        app.setChequeBookIssued(chequeBookIssued != null);
        return "redirect:/customer/edit/documents";
    }

    @GetMapping("/documents")
    public String showEditStep4_Documents(Model model, @ModelAttribute("editableApplication") KycApplication app) {
        model.addAttribute("editableApplication", app);
        return "customer/edit-step4-documents";
    }


    @PostMapping("/submit")
    public String processFinalUpdate(
            @ModelAttribute("editableApplication") KycApplication appFromSession,
            // We no longer need @ModelAttribute("nomineeDTO") here
            @RequestParam("passportPhoto") MultipartFile passportPhoto,
            @RequestParam("panDoc") MultipartFile panDoc,
            @RequestParam("aadhaarDoc") MultipartFile aadhaarDoc,
            SessionStatus sessionStatus,
            RedirectAttributes redirectAttributes) {
        
        try {
            // This call now matches the new, simplified service method signature
            registrationService.updateApplication(appFromSession, passportPhoto, panDoc, aadhaarDoc);
            sessionStatus.setComplete();
            redirectAttributes.addFlashAttribute("successMessage", "Your application has been updated successfully!");
            return "redirect:/customer/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Update failed: " + e.getMessage());
            return "redirect:/customer/edit";
        }
    }
}