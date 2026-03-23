package com.bpoconnect.patterns.singleton;

import com.bpoconnect.patterns.observer.ITicketObserver;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements ITicketObserver {
    
    public EmailNotificationService() {
    }

    @Override
    public void update(String ticketId, String newStatus) {
        System.out.println("[EmailNotificationService] Sending email for Ticket " + ticketId + ". Status changed to: " + newStatus);
        if ("Resolved".equals(newStatus)) {
            System.out.println(" - Attaching Customer Satisfaction Survey Link.");
        }
    }
}
