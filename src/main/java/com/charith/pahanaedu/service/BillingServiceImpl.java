package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.BillDTO;
import com.charith.pahanaedu.dto.CustomerAccountDTO;
import com.charith.pahanaedu.entity.Bill;
import com.charith.pahanaedu.entity.BillItem;
import com.charith.pahanaedu.entity.Customer;
import com.charith.pahanaedu.entity.Item;
import com.charith.pahanaedu.exception.ResourceNotFoundException;
import com.charith.pahanaedu.repository.BillItemRepository;
import com.charith.pahanaedu.repository.BillRepository;
import com.charith.pahanaedu.repository.CustomerRepository;
import com.charith.pahanaedu.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {
    private final BillRepository billRepository;
    private final BillItemRepository billItemRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final CustomerService customerService;

    @Override
    @Transactional
    public BillDTO createBill(BillDTO billDTO) {
        Customer customer = customerRepository.findById(billDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Bill bill = new Bill();
        bill.setCustomer(customer);
        bill.setDateTime(billDTO.getDateTime());

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal calculatedTotal = BigDecimal.ZERO;
        int totalUnits = 0;

        for (BillDTO.BillItemDTO itemDTO : billDTO.getItems()) {
            Item item = itemRepository.findById(itemDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

            BillItem billItem = new BillItem();
            billItem.setItem(item);
            billItem.setQuantity(itemDTO.getQuantity());
            billItem.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));

            calculatedTotal = calculatedTotal.add(billItem.getSubtotal());
            totalUnits += itemDTO.getQuantity();

            itemService.updateItemQuantity(itemDTO.getId(), itemDTO.getQuantity());

            billItems.add(billItem);
        }

        bill.setTotal(calculatedTotal);
        Bill savedBill = billRepository.save(bill);

        billItems.forEach(billItem -> billItem.setBill(savedBill));
        billItemRepository.saveAll(billItems);

        customerService.updateCustomerUnits(customer.getId(), customer.getUnits() + totalUnits);

        return convertToDTO(savedBill, billItems);
    }

    @Override
    public BillDTO getBillById(Integer id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        List<BillItem> billItems = billItemRepository.findByBillId(id);
        return convertToDTO(bill, billItems);
    }

    @Override
    public List<BillDTO.BillItemDTO> getBillItemsByBillId(Integer billId) {
        List<BillItem> billItems = billItemRepository.findByBillId(billId);
        return billItems.stream()
                .map(this::convertToItemDTO)
                .toList();
    }

    @Override
    public List<BillDTO> getAllBills() {
        List<Bill> bills = billRepository.findAll();
        return bills.stream()
                .map(bill -> {
                    List<BillItem> billItems = billItemRepository.findByBillId(bill.getId());
                    return convertToDTO(bill, billItems);
                })
                .toList();
    }

    @Override
    public List<BillDTO> getBillsWithinDateRange(String startDate, String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);

        List<Bill> bills = billRepository.findByDateTimeBetween(start, end);
        return bills.stream()
                .map(bill -> {
                    List<BillItem> billItems = billItemRepository.findByBillId(bill.getId());
                    return convertToDTO(bill, billItems);
                })
                .toList();
    }

    @Override
    public List<BillDTO> getBillsByCustomerId(Integer customerId) {
        List<Bill> bills = billRepository.findByCustomerId(customerId);
        return bills.stream()
                .map(bill -> {
                    List<BillItem> billItems = billItemRepository.findByBillId(bill.getId());
                    return convertToDTO(bill, billItems);
                })
                .toList();
    }

    @Override
    public CustomerAccountDTO getCustomerAccountSummary(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        BigDecimal totalSpent = billRepository.findTotalSpentByCustomerId(customerId);
        if (totalSpent == null) {
            totalSpent = BigDecimal.ZERO;
        }

        CustomerAccountDTO dto = new CustomerAccountDTO();
        dto.setCustomerId(customer.getId());
        dto.setCustomerName(customer.getName());
        dto.setTotalUnits(customer.getUnits());
        dto.setTotalSpent(totalSpent);

        return dto;
    }

    private BillDTO convertToDTO(Bill bill, List<BillItem> billItems) {
        List<BillDTO.BillItemDTO> itemDTOs = billItems.stream()
                .map(this::convertToItemDTO)
                .toList();

        return BillDTO.builder()
                .id(bill.getId())
                .customerId(bill.getCustomer().getId())
                .customerAccountNumber(bill.getCustomer().getAccountNumber())
                .customerName(bill.getCustomer().getName())
                .customerAddress(bill.getCustomer().getAddress())
                .customerPhone(bill.getCustomer().getTelephone())
                .dateTime(bill.getDateTime())
                .total(bill.getTotal())
                .items(itemDTOs)
                .build();
    }

    private BillDTO.BillItemDTO convertToItemDTO(BillItem billItem) {
        return BillDTO.BillItemDTO.builder()
                .id(billItem.getItem().getId())
                .name(billItem.getItem().getName())
                .description(billItem.getItem().getDescription())
                .quantity(billItem.getQuantity())
                .unitPrice(billItem.getItem().getPrice().toString())
                .subtotal(billItem.getSubtotal().toString())
                .build();
    }
}
