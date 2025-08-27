package com.charith.pahanaedu.controller;

import com.charith.pahanaedu.dto.*;
import com.charith.pahanaedu.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/sales")
    public ResponseEntity<List<SalesReportDTO>> getSalesReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String period,
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) Integer itemId) {

        ReportFiltersDTO filters = ReportFiltersDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .period(period)
                .customerId(customerId)
                .itemId(itemId)
                .build();

        List<SalesReportDTO> reports = reportService.getSalesReports(filters);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerReportDTO>> getCustomerReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String period,
            @RequestParam(required = false) Integer customerId) {

        ReportFiltersDTO filters = ReportFiltersDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .period(period)
                .customerId(customerId)
                .build();

        List<CustomerReportDTO> reports = reportService.getCustomerReports(filters);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryReportDTO>> getInventoryReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String period,
            @RequestParam(required = false) Integer itemId) {

        ReportFiltersDTO filters = ReportFiltersDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .period(period)
                .itemId(itemId)
                .build();

        List<InventoryReportDTO> reports = reportService.getInventoryReports(filters);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryDTO> getReportSummary(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String period) {

        ReportFiltersDTO filters = ReportFiltersDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .period(period)
                .build();

        ReportSummaryDTO summary = reportService.getReportSummary(filters);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/top-items")
    public ResponseEntity<List<TopPerformingItemDTO>> getTopPerformingItems(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") Integer limit) {

        List<TopPerformingItemDTO> topItems = reportService.getTopPerformingItems(startDate, endDate, limit);
        return ResponseEntity.ok(topItems);
    }

    @GetMapping("/customer-trends")
    public ResponseEntity<List<CustomerActivityTrendDTO>> getCustomerActivityTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String period) {

        List<CustomerActivityTrendDTO> trends = reportService.getCustomerActivityTrends(startDate, endDate, period);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryReportDTO>> getLowStockAlerts(
            @RequestParam(defaultValue = "10") Integer threshold) {

        List<InventoryReportDTO> lowStockItems = reportService.getLowStockAlerts(threshold);
        return ResponseEntity.ok(lowStockItems);
    }
}
