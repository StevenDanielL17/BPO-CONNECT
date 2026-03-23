package com.bpoconnect;

import com.bpoconnect.model.*;
import com.bpoconnect.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository,
            CustomerRepository customerRepository,
            KnowledgeBaseRepository kbRepository) {
        return args -> {
            // ----- Seed Users (Agents, Leaders, QA, Admins) -----
            // Agents
            userRepository.save(new SupportAgent("A101", "agent_sarah", "sarah.connor@bpo.com", "pass123", "T01"));
            userRepository.save(new SupportAgent("A102", "agent_john", "john.doe@bpo.com", "pass123", "T01"));
            userRepository.save(new SupportAgent("A103", "agent_emily", "emily.rose@bpo.com", "pass123", "T02"));
            userRepository.save(new SupportAgent("A104", "agent_mike", "mike.smith@bpo.com", "pass123", "T02"));

            // Team Leaders
            userRepository.save(new TeamLeader("L201", "leader_alex", "alex.turner@bpo.com", "admin456", "T01"));
            userRepository.save(new TeamLeader("L202", "leader_jessica", "jessica.alba@bpo.com", "admin456", "T02"));

            // Quality Analysts
            userRepository.save(new QualityAnalyst("Q301", "qa_david", "david.copper@bpo.com", "qa789"));
            userRepository.save(new QualityAnalyst("Q302", "qa_sophia", "sophia.loren@bpo.com", "qa789"));

            // System Admins
            userRepository.save(new SystemAdmin("AD99", "admin_super", "admin@bpo.com", "admin123"));
            userRepository.save(new SystemAdmin("AD100", "admin_network", "network@bpo.com", "admin123"));

            // ----- Seed Customers -----
            customerRepository.save(new Customer("C001", "Alice Wonderland", "alice@example.com", "555-0101"));
            customerRepository.save(new Customer("C002", "Bob Builder", "bob@example.com", "555-0102"));
            customerRepository.save(new Customer("C003", "Charlie Chaplin", "charlie@example.com", "555-0103"));
            customerRepository.save(new Customer("C004", "Diana Prince", "diana@example.com", "555-0104"));
            customerRepository.save(new Customer("C005", "Bruce Wayne", "bruce@example.com", "555-0105"));

            // ----- Seed Knowledge Base Articles -----
            kbRepository.save(new KnowledgeBaseArticle("KB001", "Resetting Password",
                    "To reset your password, click 'Forgot Password' on the login screen and follow the email instructions. If the email doesn't arrive within 5 minutes, check your spam folder.",
                    "Account Management"));
            kbRepository.save(new KnowledgeBaseArticle("KB002", "Checking Ticket Status",
                    "You can check your ticket status by logging into the portal and navigating to the 'My Tickets' dashboard. The status will be updated in real-time.",
                    "Support"));
            kbRepository.save(new KnowledgeBaseArticle("KB003", "Connection Issues",
                    "If you are experiencing slow connection speeds, first try restarting your modem. If the issue persists, contact your ISP to check for local outages.",
                    "Technical Support"));
            kbRepository.save(new KnowledgeBaseArticle("KB004", "Billing Inquiries",
                    "For questions regarding your recent invoice, please review the breakdown in the billing section. Remember that partial month charges may apply if you recently changed plans.",
                    "Billing"));
            kbRepository.save(new KnowledgeBaseArticle("KB005", "Updating Account Info",
                    "To update your billing address or payment method, navigate to Settings > Account Info. Changes take effect immediately.",
                    "Account Management"));
        };
    }
}
