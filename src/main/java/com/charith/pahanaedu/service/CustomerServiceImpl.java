package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.CustomerDTO;
import com.charith.pahanaedu.entity.Customer;
import com.charith.pahanaedu.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final SecureRandom secureRandom;

    @Override
    public Customer createCustomer(CustomerDTO createCustomerDto) {
        Customer customer = new Customer();
        customer.setAccountNumber(generateUniqueAccountNumber());
        customer.setName(createCustomerDto.getName());
        customer.setAddress(createCustomerDto.getAddress());
        customer.setTelephone(createCustomerDto.getTelephone());
        customer.setUnits(0);
        customer.setCreatedAt(LocalDateTime.now());

        return customerRepository.save(customer);
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        int attempts = 0;
        int maxAttempts = 5;

        do {
            int number = 10000 + secureRandom.nextInt(90000);
            accountNumber = String.valueOf(number);
            attempts++;

            if (attempts >= maxAttempts) {
                throw new RuntimeException("Unable to generate unique account number after " + maxAttempts + " attempts");
            }
        } while (customerRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    @Override
    public Customer updateCustomer(String accountNo, CustomerDTO updateCustomerDto) {
        Customer customer = customerRepository.findByAccountNumber(accountNo)
                .orElseThrow(() -> new RuntimeException("Customer not found with account number: " + accountNo));

        if (updateCustomerDto.getName() != null && !updateCustomerDto.getName().trim().isEmpty()) {
            customer.setName(updateCustomerDto.getName());
        }
        if (updateCustomerDto.getAddress() != null && !updateCustomerDto.getAddress().trim().isEmpty()) {
            customer.setAddress(updateCustomerDto.getAddress());
        }
        if (updateCustomerDto.getTelephone() != null && !updateCustomerDto.getTelephone().trim().isEmpty()) {
            customer.setTelephone(updateCustomerDto.getTelephone());
        }

        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomerUnits(Integer id, Integer units) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setUnits(units);

        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    @Override
    public Customer getCustomerByAccountNumber(String accountNumber) {
        return customerRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Customer not found with account number: " + accountNumber));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.findByNameOrTelephoneOrAccountNumber(keyword);
    }
}
