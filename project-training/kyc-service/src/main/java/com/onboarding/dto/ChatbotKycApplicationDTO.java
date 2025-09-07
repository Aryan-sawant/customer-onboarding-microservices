package com.onboarding.dto;

import com.onboarding.model.KycApplication;
import com.onboarding.model.KycStatus;
import java.time.LocalDate;

// This DTO contains only the information the chatbot needs, without large file data.
public class ChatbotKycApplicationDTO {

    private Long id;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String maritalStatus;
    private String fathersName;
    private String nationality;
    private String profession;
    private String address;
    private String email;
    private String phone;
    private String pan;
    private String aadhaar;
    private String requestedAccountType;
    private String username;
    private KycStatus kycStatus;
    private Boolean netBankingEnabled;
    private Boolean debitCardIssued;
    private Boolean chequeBookIssued;
    
    // --- The key change: Replace Base64 strings with boolean flags ---
    private boolean hasPassportPhoto;
    private boolean hasPanDocument;
    private boolean hasAadhaarDocument;
    
    // --- A nested DTO for the nominee ---
    private NomineeDTO nominee;

    // Static factory method to convert from the full entity to this lightweight DTO
    public static ChatbotKycApplicationDTO fromEntity(KycApplication app) {
        if (app == null) return null;
        
        ChatbotKycApplicationDTO dto = new ChatbotKycApplicationDTO();
        dto.setId(app.getId());
        dto.setFullName(app.getFullName());
        dto.setDob(app.getDob());
        dto.setGender(app.getGender());
        dto.setMaritalStatus(app.getMaritalStatus());
        dto.setFathersName(app.getFathersName());
        dto.setNationality(app.getNationality());
        dto.setProfession(app.getProfession());
        dto.setAddress(app.getAddress());
        dto.setEmail(app.getEmail());
        dto.setPhone(app.getPhone());
        dto.setPan(app.getPan());
        dto.setAadhaar(app.getAadhaar());
        dto.setRequestedAccountType(app.getRequestedAccountType());
        dto.setUsername(app.getUsername());
        dto.setKycStatus(app.getKycStatus());
        dto.setNetBankingEnabled(app.getNetBankingEnabled());
        dto.setDebitCardIssued(app.getDebitCardIssued());
        dto.setChequeBookIssued(app.getChequeBookIssued());
        
        // Set boolean flags based on whether the Base64 string exists
        dto.setHasPassportPhoto(app.getPassportPhotoBase64() != null && !app.getPassportPhotoBase64().isEmpty());
        dto.setHasPanDocument(app.getPanPhotoBase64() != null && !app.getPanPhotoBase64().isEmpty());
        dto.setHasAadhaarDocument(app.getAadhaarPhotoBase64() != null && !app.getAadhaarPhotoBase64().isEmpty());
        
        if (app.getKycNominee() != null) {
            NomineeDTO nomineeDTO = new NomineeDTO();
            nomineeDTO.setName(app.getKycNominee().getName());
            nomineeDTO.setMobile(app.getKycNominee().getMobile());
            nomineeDTO.setAddress(app.getKycNominee().getAddress());
            nomineeDTO.setAadhaarNumber(app.getKycNominee().getAadhaarNumber());
            dto.setNominee(nomineeDTO);
        }
        
        return dto;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getFathersName() {
		return fathersName;
	}

	public void setFathersName(String fathersName) {
		this.fathersName = fathersName;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getAadhaar() {
		return aadhaar;
	}

	public void setAadhaar(String aadhaar) {
		this.aadhaar = aadhaar;
	}

	public String getRequestedAccountType() {
		return requestedAccountType;
	}

	public void setRequestedAccountType(String requestedAccountType) {
		this.requestedAccountType = requestedAccountType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public KycStatus getKycStatus() {
		return kycStatus;
	}

	public void setKycStatus(KycStatus kycStatus) {
		this.kycStatus = kycStatus;
	}

	public Boolean getNetBankingEnabled() {
		return netBankingEnabled;
	}

	public void setNetBankingEnabled(Boolean netBankingEnabled) {
		this.netBankingEnabled = netBankingEnabled;
	}

	public Boolean getDebitCardIssued() {
		return debitCardIssued;
	}

	public void setDebitCardIssued(Boolean debitCardIssued) {
		this.debitCardIssued = debitCardIssued;
	}

	public Boolean getChequeBookIssued() {
		return chequeBookIssued;
	}

	public void setChequeBookIssued(Boolean chequeBookIssued) {
		this.chequeBookIssued = chequeBookIssued;
	}

	public boolean isHasPassportPhoto() {
		return hasPassportPhoto;
	}

	public void setHasPassportPhoto(boolean hasPassportPhoto) {
		this.hasPassportPhoto = hasPassportPhoto;
	}

	public boolean isHasPanDocument() {
		return hasPanDocument;
	}

	public void setHasPanDocument(boolean hasPanDocument) {
		this.hasPanDocument = hasPanDocument;
	}

	public boolean isHasAadhaarDocument() {
		return hasAadhaarDocument;
	}

	public void setHasAadhaarDocument(boolean hasAadhaarDocument) {
		this.hasAadhaarDocument = hasAadhaarDocument;
	}

	public NomineeDTO getNominee() {
		return nominee;
	}

	public void setNominee(NomineeDTO nominee) {
		this.nominee = nominee;
	}
}