package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.BillDTO;
import com.charith.pahanaedu.dto.CustomerAccountDTO;
import com.charith.pahanaedu.entity.Bill;
import com.charith.pahanaedu.entity.Customer;
import com.charith.pahanaedu.entity.Item;
import com.charith.pahanaedu.exception.ResourceNotFoundException;
import com.charith.pahanaedu.repository.BillItemRepository;
import com.charith.pahanaedu.repository.BillRepository;
import com.charith.pahanaedu.repository.CustomerRepository;
import com.charith.pahanaedu.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BillingServiceImplTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private BillItemRepository billItemRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private CustomerService customerService;

    @InjectMocks
    private BillingServiceImpl billingService;

    private Customer testCustomer;
    private Item testItem;
    private Bill testBill;
    private BillDTO testBillDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testCustomer = new Customer();
        testCustomer.setId(1);
        testCustomer.setName("John Doe");
        testCustomer.setUnits(100);

        testItem = new Item();
        testItem.setId(1);
        testItem.setName("Test Item");
        testItem.setPrice(new BigDecimal("10.50"));

        testBill = new Bill();
        testBill.setId(1);
        testBill.setCustomer(testCustomer);
        testBill.setDateTime(LocalDateTime.now());
        testBill.setTotal(new BigDecimal("21.00"));

        BillDTO.BillItemDTO itemDTO = BillDTO.BillItemDTO.builder()
                .id(1)
                .quantity(2)
                .build();

        testBillDTO = BillDTO.builder()
                .customerId(1)
                .dateTime(LocalDateTime.now())
                .items(List.of(itemDTO))
                .build();
    }

    @Test
    void shouldCreateBillSuccessfully() {
        // Given
        given(customerRepository.findById(1)).willReturn(Optional.of(testCustomer));
        given(itemRepository.findById(1)).willReturn(Optional.of(testItem));
        given(billRepository.save(any(Bill.class))).willReturn(testBill);

        // When
        BillDTO result = billingService.createBill(testBillDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getCustomerId()).isEqualTo(1);
        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("21.00"));

        // Verify interactions
        then(billRepository).should().save(any(Bill.class));
        then(billItemRepository).should().saveAll(any());
        then(itemService).should().updateItemQuantity(1, 2);
        then(customerService).should().updateCustomerUnits(1, 102);
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        given(customerRepository.findById(1)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> billingService.createBill(testBillDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer not found");

        // Verify no save operations occurred
        then(billRepository).should(never()).save(any(Bill.class));
    }

    @Test
    void shouldGetCustomerAccountSummarySuccessfully() {
        // Given
        BigDecimal totalSpent = new BigDecimal("150.00");
        given(customerRepository.findById(1)).willReturn(Optional.of(testCustomer));
        given(billRepository.findTotalSpentByCustomerId(1)).willReturn(totalSpent);

        // When
        CustomerAccountDTO result = billingService.getCustomerAccountSummary(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(1);
        assertThat(result.getCustomerName()).isEqualTo("John Doe");
        assertThat(result.getTotalUnits()).isEqualTo(100);
        assertThat(result.getTotalSpent()).isEqualByComparingTo(totalSpent);
    }
}