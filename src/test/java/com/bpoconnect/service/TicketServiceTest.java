package com.bpoconnect.service;

import com.bpoconnect.model.SLA;
import com.bpoconnect.model.Ticket;
import com.bpoconnect.model.VoiceTicket;
import com.bpoconnect.patterns.factory.TicketFactory;
import com.bpoconnect.patterns.observer.ITicketObserver;
import com.bpoconnect.repository.SLARepository;
import com.bpoconnect.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    @Mock
    private TicketFactory ticketFactory;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private SLARepository slaRepository;

    @Mock
    private List<ITicketObserver> observers;

    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Ensure observers list is not null to avoid NPE in notifyObservers
        try {
            java.lang.reflect.Field field = TicketService.class.getDeclaredField("observers");
            field.setAccessible(true);
            field.set(ticketService, new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testCreateTicket() {
        // Arrange
        String channel = "Voice";
        String customerId = "C101";
        String agentId = "A101";
        String severity = "High";
        String description = "Test ticket";
        String referenceId = "REF001";

        Ticket mockedTicket = new VoiceTicket("T001", customerId, agentId, severity, description, referenceId, "Unknown");
        
        when(ticketFactory.createTicket(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockedTicket);
        
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockedTicket);

        // Act
        Ticket result = ticketService.createTicket(channel, customerId, agentId, severity, description, referenceId);

        // Assert
        assertNotNull(result);
        assertEquals("T001", result.getTicketId());
        assertEquals("High", result.getSeverity());
        
        verify(ticketFactory, times(1)).createTicket(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(slaRepository, times(1)).save(any(SLA.class));
    }
}
