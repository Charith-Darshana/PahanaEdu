package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.*;
import com.charith.pahanaedu.entity.Item;
import com.charith.pahanaedu.repository.BillItemRepository;
import com.charith.pahanaedu.repository.BillRepository;
import com.charith.pahanaedu.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final BillRepository billRepository;
    private final ItemRepository itemRepository;
    private final BillItemRepository billItemRepository;

    public List<SalesReportDTO> getSalesReports(ReportFiltersDTO filters) {
        LocalDateTime startDateTime = filters.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = filters.getEndDate().atTime(23, 59, 59);

        List<Object[]> salesData = billRepository.findSalesReports(startDateTime, endDateTime, filters.getPeriod());

        return salesData.stream()
                .map((data) -> {
                    return mapToSalesReportDto(data, filters.getPeriod());
                })
                .toList();
    }

    public List<CustomerReportDTO> getCustomerReports(ReportFiltersDTO filters) {
        LocalDateTime startDateTime = filters.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = filters.getEndDate().atTime(23, 59, 59);

        List<Object[]> customerData;

        if (filters.getCustomerId() != null) {
            customerData = billRepository.findCustomerReportById(
                    startDateTime, endDateTime, filters.getCustomerId());
        } else {
            customerData = billRepository.findCustomerReports(startDateTime, endDateTime);
        }

        return customerData.stream()
                .map(this::mapToCustomerReportDto)
                .toList();
    }

    public List<InventoryReportDTO> getInventoryReports(ReportFiltersDTO filters) {
        LocalDateTime startDateTime = filters.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = filters.getEndDate().atTime(23, 59, 59);

        List<Object[]> inventoryData;

        if (filters.getItemId() != null) {
            inventoryData = itemRepository.findInventoryReportById(
                    startDateTime, endDateTime, filters.getItemId());
        } else {
            inventoryData = itemRepository.findInventoryReports(startDateTime, endDateTime);
        }

        return inventoryData.stream()
                .map(this::mapToInventoryReportDto)
                .toList();
    }

    public ReportSummaryDTO getReportSummary(ReportFiltersDTO filters) {
        LocalDateTime startDateTime = filters.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = filters.getEndDate().atTime(23, 59, 59);

        Object[] summaryData = (Object[]) billRepository.findReportSummary(startDateTime, endDateTime);

        if (summaryData == null || summaryData.length == 0) {
            return ReportSummaryDTO.builder()
                    .totalRevenue(BigDecimal.ZERO)
                    .totalOrders(0L)
                    .totalQuantitySold(0L)
                    .averageOrderValue(BigDecimal.ZERO)
                    .totalCustomers(0L)
                    .totalItems(0L)
                    .build();
        }

        BigDecimal totalRevenue = (BigDecimal) summaryData[0];
        long totalOrders = ((Number) summaryData[1]).longValue();
        Long totalQuantity = ((Number) summaryData[2]).longValue();
        Long totalCustomers = ((Number) summaryData[3]).longValue();
        Long totalItems = itemRepository.count();

        BigDecimal averageOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return ReportSummaryDTO.builder()
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .totalQuantitySold(totalQuantity)
                .averageOrderValue(averageOrderValue)
                .totalCustomers(totalCustomers)
                .totalItems(totalItems)
                .build();
    }

    public List<TopPerformingItemDTO> getTopPerformingItems(LocalDate startDate, LocalDate endDate, Integer limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> topItemsData = billRepository.findTopPerformingItems(startDateTime, endDateTime, limit);

        return topItemsData.stream()
                .map(data -> TopPerformingItemDTO.builder()
                        .itemId((Integer) data[0])
                        .itemName((String) data[1])
                        .totalQuantitySold(((Number) data[2]).longValue())
                        .totalRevenue((BigDecimal) data[3])
                        .orderCount(((Number) data[4]).longValue())
                        .build())
                .toList();
    }

    public List<CustomerActivityTrendDTO> getCustomerActivityTrends(LocalDate startDate, LocalDate endDate, String period) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Object[]> trendsData = billRepository.findCustomerActivityTrends(startDateTime, endDateTime, period);

        return trendsData.stream()
                .map(data -> CustomerActivityTrendDTO.builder()
                        .period((String) data[0])
                        .newCustomers(((Number) data[1]).longValue())
                        .returningCustomers(((Number) data[2]).longValue())
                        .totalOrders(((Number) data[3]).longValue())
                        .totalRevenue((BigDecimal) data[4])
                        .build())
                .toList();
    }

    public List<InventoryReportDTO> getLowStockAlerts(Integer threshold) {
        List<Item> lessItems = itemRepository.findByQuantityLessThan(threshold);

        List<InventoryReportDTO> alertItems = new ArrayList<>();

        for (Item item : lessItems) {
            InventoryReportDTO dto = InventoryReportDTO.builder()
                    .itemId(item.getId())
                    .itemName(item.getName())
                    .description(item.getDescription())
                    .currentStock(item.getQuantity())
                    .unitPrice(item.getPrice())
                    .stockValue(BigDecimal.valueOf(item.getQuantity()).multiply(item.getPrice()))
                    .build();

            Object[] itemStats = billItemRepository.findLessQuantityItemStats(item.getId());

            dto.setTotalRevenue((BigDecimal) itemStats[0]);
            dto.setTotalSold((Long) itemStats[1]);
            dto.setLastSoldDate((String) itemStats[2]);

            alertItems.add(dto);
        }

        return alertItems;
    }

    private SalesReportDTO mapToSalesReportDto(Object[] data, String period) {
        return SalesReportDTO.builder()
                .period((String) data[0])
                .totalAmount(data[1] != null ? (BigDecimal) data[1] : BigDecimal.ZERO)
                .totalQuantity(data[2] != null ? ((Number) data[2]).longValue() : 0L)
                .orderCount(data[3] != null ? ((Number) data[3]).longValue() : 0L)
                .averageOrderValue(data[4] != null ? (BigDecimal) data[4] : BigDecimal.ZERO)
                .topSellingItem(null)
                .build();
    }

    private CustomerReportDTO mapToCustomerReportDto(Object[] data) {
        return CustomerReportDTO.builder()
                .customerId((Integer) data[0])
                .customerName((String) data[1])
                .totalSpent((BigDecimal) data[2])
                .orderCount(((Number) data[3]).longValue())
                .lastOrderDate(data[4] != null ? ((Timestamp) data[4]).toLocalDateTime().toString() : null)
                .averageOrderValue((BigDecimal) data[5])
                .totalQuantityPurchased(((Number) data[6]).longValue())
                .build();
    }

    private InventoryReportDTO mapToInventoryReportDto(Object[] data) {
        return InventoryReportDTO.builder()
                .itemId((Integer) data[0])
                .itemName((String) data[1])
                .description((String) data[2])
                .currentStock((Integer) data[3])
                .totalSold(data[4] != null ? ((Number) data[4]).longValue() : 0L)
                .totalRevenue(data[5] != null ? (BigDecimal) data[5] : BigDecimal.ZERO)
                .unitPrice((BigDecimal) data[6])
                .stockValue(BigDecimal.valueOf((Integer) data[3]).multiply((BigDecimal) data[6]))
                .lastSoldDate(data[8] != null ? ((Timestamp) data[8]).toLocalDateTime().toString() : null)
                .build();
    }
}
