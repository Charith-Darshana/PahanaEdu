package com.charith.pahanaedu.service;

import com.charith.pahanaedu.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<SalesReportDTO> getSalesReports(ReportFiltersDTO filters);
    List<CustomerReportDTO> getCustomerReports(ReportFiltersDTO filters);
    List<InventoryReportDTO> getInventoryReports(ReportFiltersDTO filters);
    ReportSummaryDTO getReportSummary(ReportFiltersDTO filters);
    List<TopPerformingItemDTO> getTopPerformingItems(LocalDate startDate, LocalDate endDate, Integer limit);
    List<CustomerActivityTrendDTO> getCustomerActivityTrends(LocalDate startDate, LocalDate endDate, String period);
    List<InventoryReportDTO> getLowStockAlerts(Integer threshold);
}
