package com.bpoconnect.patterns.singleton;

import com.bpoconnect.model.Customer;
import com.bpoconnect.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScreenPopController {
    
    private final CustomerRepository customerRepository;

    @Autowired
    public ScreenPopController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer handleInboundCall(String ani) {
        System.out.println("[ScreenPopController] Inbound call detected from ANI: " + ani);
        Optional<Customer> customer = customerRepository.findByContactNumber(ani);
        if (customer.isPresent()) {
            System.out.println(" - Known Customer found: " + customer.get().getCustomerName());
            return customer.get();
        } else {
            System.out.println(" - Unknown Customer. Displaying new customer form.");
            return null;
        }
    }
}
