# Digital Banking Onboarding Platform

This project is a comprehensive, microservices-based digital onboarding platform for a modern bank. It handles the entire KYC application workflow, from submission and administrative review to automated customer and bank account creation.

## Key Features

* **Microservices Architecture:** The system is divided into five core services:
    * **KYC-Service:** Manages the multi-step user registration and KYC data collection.
    * **Customer-Service:** Handles the creation and management of permanent customer records post-approval.
    * **Account-Service:** Manages the creation and lifecycle of customer bank accounts.
    * **Eureka Server:** Provides service discovery for the microservices ecosystem.
    * **API Gateway:** Acts as a single entry point for all client requests, routing them to the appropriate service.
* **Event-Driven Workflow:** Utilizes Apache Kafka for asynchronous communication between services, ensuring a decoupled and resilient system for events like new applications and status updates.
* **AI-Powered Chatbot:** Features an integrated AI assistant (**BankChatbot**) built with Python, Flask, and the Google Gemini API. It allows administrators to query customer and application data using natural language.
* **Secure Authentication:** Implements Spring Security with distinct authentication mechanisms for the UI (form-based login) and internal APIs (HTTP Basic Auth).
* **Dynamic Front-End:** The user and admin interfaces are built with Thymeleaf, HTML5, and CSS.

## Technology Stack

* **Backend:** Java, Spring Boot, Spring Cloud (Eureka, Gateway, Feign), Spring Security, JPA (Hibernate)
* **Database:** Oracle DB
* **Messaging:** Apache Kafka
* **Frontend:** Thymeleaf, HTML5, CSS
* **AI Chatbot:** Python, Flask, Google Gemini API
* **Build Tool:** Maven

## How to Run

1.  Start the Eureka Server.
2.  Start the other Spring Boot microservices (KYC, Customer, Account, API Gateway).
3.  Run the Python Flask application for the BankChatbot.
4.  Access the application through the API Gateway's port (default: 8080).