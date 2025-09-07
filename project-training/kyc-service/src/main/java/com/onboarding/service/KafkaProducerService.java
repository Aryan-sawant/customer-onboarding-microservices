package com.onboarding.service;

import com.onboarding.config.KafkaTopicConfig;
import com.onboarding.dto.KycStatusUpdateEvent;
import com.onboarding.dto.NewKycApplicationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendNewKycApplicationNotification(NewKycApplicationEvent event) {
        LOGGER.info("Publishing new KYC application event for applicant: {}", event.getApplicantName());
        kafkaTemplate.send(KafkaTopicConfig.TOPIC_CUSTOMER_REGISTRATION, event);
    }

    public void sendKycUpdateNotification(KycStatusUpdateEvent event) {
        LOGGER.info("Publishing KYC status update event for customer: {}", event.getCustomerName());
        kafkaTemplate.send(KafkaTopicConfig.TOPIC_KYC_STATUS_UPDATE, event);
    }
}