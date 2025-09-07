package com.onboarding.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    /**
     * Creates a request interceptor that adds a Basic Authentication header
     * to every outgoing Feign request. This is how the kyc-service authenticates
     * itself when calling the customer-service's internal APIs.
     *
     * @return A configured BasicAuthRequestInterceptor bean.
     */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        // These credentials MUST MATCH the 'internal-user' we created
        // in the customer-service's DataLoader.
        // In a production environment, these should come from a secure vault or
        // environment variables, not be hardcoded.
        return new BasicAuthRequestInterceptor("internal-user", "internal-password");
    }
}