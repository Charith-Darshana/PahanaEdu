package com.charith.pahanaedu.controller;

import com.charith.pahanaedu.dto.BillDTO;
import com.charith.pahanaedu.dto.CustomerAccountDTO;
import com.charith.pahanaedu.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;

    @PostMapping("/bills/create")
    public ResponseEntity<BillDTO> createBill(@RequestBody BillDTO billDTO) {
        BillDTO createdBill = billingService.createBill(billDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBill);
    }

    @GetMapping("/bills/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Integer id) {
        BillDTO bill = billingService.getBillById(id);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/bills/{billId}/items")
    public ResponseEntity<List<BillDTO.BillItemDTO>> getBillItemsByBillId(@PathVariable Integer billId) {
        List<BillDTO.BillItemDTO> billItems = billingService.getBillItemsByBillId(billId);
        return ResponseEntity.ok(billItems);
    }

    @GetMapping("/bills/all")
    public ResponseEntity<List<BillDTO>> getAllBills() {
        List<BillDTO> bills = billingService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/bills/date-range")
    public ResponseEntity<List<BillDTO>> getBillsWithinDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<BillDTO> bills = billingService.getBillsWithinDateRange(startDate, endDate);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/bills/customer/{customerId}")
    public ResponseEntity<List<BillDTO>> getBillsByCustomerId(@PathVariable Integer customerId) {
        List<BillDTO> bills = billingService.getBillsByCustomerId(customerId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/customer/{customerId}/account-summary")
    public ResponseEntity<CustomerAccountDTO> getCustomerAccountSummary(@PathVariable Integer customerId) {
        CustomerAccountDTO accountSummary = billingService.getCustomerAccountSummary(customerId);
        return ResponseEntity.ok(accountSummary);
    }
}
