package com.charith.pahanaedu.controller;

import com.charith.pahanaedu.dto.CustomerDTO;
import com.charith.pahanaedu.entity.Customer;
import com.charith.pahanaedu.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerDTO createCustomerDto) {
        Customer createdCustomer = customerService.createCustomer(createCustomerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PutMapping("/{accountNo}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String accountNo, @RequestBody CustomerDTO updateCustomerDto) {
        Customer updatedCustomer = customerService.updateCustomer(accountNo, updateCustomerDto);
        return ResponseEntity.ok(updatedCustomer);
    }

    @PatchMapping("/{id}/units/{units}")
    public ResponseEntity<Customer> updateCustomerUnits(@PathVariable Integer id, @PathVariable Integer units) {
        Customer updatedCustomer = customerService.updateCustomerUnits(id, units);
        return ResponseEntity.ok(updatedCustomer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<Customer> getCustomerByAccountNumber(@PathVariable String accountNumber) {
        Customer customer = customerService.getCustomerByAccountNumber(accountNumber);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String keyword) {
        List<Customer> customers = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(customers);
    }
}
