package com.bpoconnect.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
@SuppressWarnings("unused")
public class Customer {
    @Id
    private String customerId;
    private String customerName;
    private String email;
    private String contactNumber;
    private String accountStatus;

    public Customer() {} // No-arg constructor for JPA

    public Customer(String customerId, String customerName, String email, String contactNumber) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.accountStatus = "Active";
    }

    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getContactNumber() { return contactNumber; }
}


