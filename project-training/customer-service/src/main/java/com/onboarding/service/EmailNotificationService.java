package com.onboarding.service;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.onboarding.config.KafkaTopicConfig;
import com.onboarding.dto.KycStatusUpdateEvent;
import com.onboarding.dto.NewKycApplicationEvent;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final String ADMIN_EMAIL = "umeshsawant112233@gmail.com";
    private static final String SENDER_EMAIL = "umeshsawant112233@gmail.com";
    private final JavaMailSender javaMailSender;
    public EmailNotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    /**
     * Listens for new KYC application events from Kafka.
     */
    @KafkaListener(
            topics = KafkaTopicConfig.TOPIC_CUSTOMER_REGISTRATION,
            groupId = "onboarding_group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeNewKycApplicationEvent(NewKycApplicationEvent event) {
        LOGGER.info("--> Received New KYC Application Event for ID: {}", event.getKycApplicationId());
        String subject = "New Customer Registration Pending KYC";
        String adminDashboardLink = "http://localhost:8080/login" + event.getKycApplicationId();
        String htmlBody = "<html><body>" +
                "<img src='cid:favicon'>" +
                "<h2>New Customer Verification</h2>" +
                "<p>Hello Admin,</p>" +
                "<p>A new customer has been submitted by, <b>" + event.getApplicantName() + "</b> (Application ID: " + event.getKycApplicationId() + "),  and is awaiting KYC verification.</p>" +
                "<hr>" +
                "<p>To review the applicant's details,please follow the link to the secure Admin Dashboard</p>" +
                "<p style=\"text-align: center;\"><a href=\"" + adminDashboardLink + "\" style=\"background-color: #007BFF; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; display: inline-block;\">Review Application</a></p>" +
                "<p>Regards,<br>The OFSS Onboarding System</p>" +
                "</body></html>";
        try {
            sendHtmlEmail(ADMIN_EMAIL, subject, htmlBody);
            LOGGER.info("Successfully sent new application notification email to admin for app ID: {}", event.getKycApplicationId());
        } catch (MessagingException | IOException e) {
            LOGGER.error("Failed to send new application notification email to admin for app ID: {}. Error: {}", event.getKycApplicationId(), e.getMessage());
        }
    }
    /**
     * Listens for KYC status update events from Kafka.
     */
    @KafkaListener(
            topics = KafkaTopicConfig.TOPIC_KYC_STATUS_UPDATE,
            groupId = "onboarding_group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeKycStatusUpdate(KycStatusUpdateEvent event) {
        LOGGER.info("--> Received KYC Status Update Event for: {}", event.getCustomerName());
        String customerLoginLink = "http://localhost:8080/login";
        String subject;
        String htmlBody;
        if ("VERIFIED".equalsIgnoreCase(event.getKycStatus())) {
            subject = "Congratulations! Your KYC with OFSS Bank has been Verified";
            htmlBody = "<html><body>" +
                    "<img src='cid:favicon'>" +
                    "<p>Dear Ms. " + event.getCustomerName() + ",</p>" +
                    "<p>Thank you for completing your KYC verification with OFSS Bank. We are pleased to inform you that your verification was successful.</p>" +
                    "<hr>" +
                    "<p style='font-weight: bold;'>Your Account Details:</p>" +
                    "<ul>" +
                    "<li>Account Number: " + event.getAccountNumber() + "</li>" +
                    "<li>Account Type: " + event.getAccountType() + "</li>" +
                    "<li>IFSC Code: " + event.getIfscCode() + "</li>" +
                    "</ul>" +
                    "<p>You can now log in to your customer dashboard here:<br><a href=\"" + customerLoginLink + "\">Customer Dashboard</a></p>" +
                    "<p>We look forward to providing you with excellent service.</p>" +
                    "<p>Warm regards,<br>OFSS Onboarding Team</p>" +
                    "</body></html>";
        } else { // REJECTED
            subject = "Important: Update on Your KYC Verification with OFSS Bank";
            String rejectionReason = event.getRejectionReason() != null ? event.getRejectionReason() : "Mismatched information or unclear documents.";
            htmlBody = "<html><body>" +
                    "<img src='cid:favicon'>" +
                    "<p>Dear Ms. " + event.getCustomerName() + ",</p>" +
                    "<p>Thank you for submitting your KYC documents to OFSS Bank. We truly value your interest in banking with us and appreciate the time you took to complete your application.</p>" +
                    "<p>After a thorough review of your submission, we regret to inform you that your <b>KYC verification has not been approved</b> at this time.</p>" +
                    "<hr>" +
                    "<p style='font-weight: bold;'>Reason for Rejection:</p>" +
                    "<p><b>" + rejectionReason + "</b></p>" +
                    "<hr>" +
                    "<p style='font-weight: bold;'>Next Steps:</p>" +
                    "<ol>" +
                    "<li>Upload a valid and clearly visible Photo ID (e.g., Aadhaar Card, PAN Card, Passport, or Driving License).</li>" +
                    "<li>Ensure the document is not cropped, all details are legible, and the image is well-lit.</li>" +
                    "<li>Resubmit your documents through your onboarding dashboard.</li>" +
                    "</ol>" +
                    "<hr>" +
                    "<p style='font-weight: bold;'>Need Help?</p>" +
                    "<p>Our support team is here to help you:<br>" +
                    "&#x2709;&#xFE0F; Email: support@ofssbank.com<br>" +
                    "&#x260E;&#xFE0F; Phone: 1800-123-4567 (Mon–Sat, 9 AM to 6 PM)</p>" +
                    "<p>We encourage you to reapply at your earliest convenience so we can proceed with creating your account.</p>" +
                    "<p>Thank you for your patience and understanding.</p>" +
                    "<p>Warm regards,<br>OFSS Onboarding Team</p>" +
                    "</body></html>";
        }
        try {
            sendHtmlEmail(event.getCustomerEmail(), subject, htmlBody);
            LOGGER.info("Successfully sent KYC status update email to customer: {}", event.getCustomerName());
        } catch (MessagingException | IOException e) {
            LOGGER.error("Failed to send KYC status update email to customer: {}. Error: {}", event.getCustomerName(), e.getMessage());
        }
    }
    /**
     * Helper method to send an HTML email with an embedded image.
     */
    private void sendHtmlEmail(String to, String subject, String htmlText) throws MessagingException, IOException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // 'true' enables multipart/mixed mode for attachments/inline resources
        helper.setFrom(SENDER_EMAIL);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlText, true); // The 'true' flag here is crucial; it tells the helper to treat the text as HTML
        // Add the favicon.ico from the static folder as an inline image
        javaMailSender.send(message);
    }
}