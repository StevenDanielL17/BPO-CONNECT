package com.bpoconnect.patterns.observer;

public interface ITicketObserver {
    void update(String ticketId, String newStatus);
}
