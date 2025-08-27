package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.CustomerDTO;
import com.charith.pahanaedu.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(CustomerDTO customerDTO);
    Customer updateCustomer(String accountNo, CustomerDTO customerDTO);
    Customer updateCustomerUnits(Integer customerId, Integer units);
    Customer getCustomerById(Integer customerId);
    Customer getCustomerByAccountNumber(String accountNumber);
    List<Customer> getAllCustomers();
    List<Customer> searchCustomers(String keyword);
}
