package com.bpoconnect.service;

import org.springframework.stereotype.Service;
@Service public class NotificationService { private static NotificationService instance = new NotificationService(); private NotificationService(){} public static NotificationService getInstance(){return instance;} public void sendTicketCreatedEmail(){} public void sendTicketResolvedEmail(){} }
