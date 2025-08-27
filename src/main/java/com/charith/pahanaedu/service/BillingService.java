package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.BillDTO;
import com.charith.pahanaedu.dto.CustomerAccountDTO;

import java.util.List;

public interface BillingService {
    BillDTO createBill(BillDTO billDTO);
    BillDTO getBillById(Integer id);
    List<BillDTO.BillItemDTO> getBillItemsByBillId(Integer billId);
    List<BillDTO> getAllBills();
    List<BillDTO> getBillsWithinDateRange(String startDate, String endDate);
    List<BillDTO> getBillsByCustomerId(Integer customerId);
    CustomerAccountDTO getCustomerAccountSummary(Integer customerId);
}
