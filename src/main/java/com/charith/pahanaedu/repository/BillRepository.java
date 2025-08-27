package com.charith.pahanaedu.repository;

import com.charith.pahanaedu.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    List<Bill> findByCustomerId(Integer customerId);
    List<Bill> findByDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(b.total) FROM Bill b WHERE b.customer.id = :customerId")
    BigDecimal findTotalSpentByCustomerId(@Param("customerId") Integer customerId);

    @Query(value = """
            SELECT 
                CASE 
                    WHEN :period = 'day' THEN DATE_FORMAT(b.date_time, '%Y-%m-%d')
                    WHEN :period = 'week' THEN CONCAT(YEAR(b.date_time), '-W', LPAD(WEEK(b.date_time, 1), 2, '0'))
                    WHEN :period = 'month' THEN DATE_FORMAT(b.date_time, '%Y-%m')
                    WHEN :period = 'quarter' THEN CONCAT(YEAR(b.date_time), '-Q', QUARTER(b.date_time))
                    WHEN :period = 'year' THEN DATE_FORMAT(b.date_time, '%Y')
                END AS period,
                SUM(bi.subtotal) AS totalAmount,
                COALESCE(SUM(bi.quantity), 0) AS totalQuantity,
                COUNT(DISTINCT b.id) AS orderCount,
                CASE WHEN COUNT(DISTINCT b.id) > 0 THEN SUM(bi.subtotal) / COUNT(DISTINCT b.id) ELSE 0 END AS averageOrderValue
            FROM bills b
            LEFT JOIN bill_items bi ON b.id = bi.bill_id
            WHERE b.date_time BETWEEN :startDate AND :endDate
            GROUP BY period
            ORDER BY period
        """, nativeQuery = true)
    List<Object[]> findSalesReports(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("period") String period);


    @Query(value = """
            SELECT
                c.id AS customerId,
                c.name AS customerName,
                COALESCE(bill_totals.totalSpent, 0) AS totalSpent,
                COALESCE(bill_totals.orderCount, 0) AS orderCount,
                bill_totals.lastOrderDate,
                CASE WHEN COALESCE(bill_totals.orderCount, 0) > 0
                     THEN COALESCE(bill_totals.totalSpent / bill_totals.orderCount, 0)
                     ELSE 0 END AS averageOrderValue,
                COALESCE(items_totals.totalQuantityPurchased, 0) AS totalQuantityPurchased
            FROM customers c
            LEFT JOIN (
                SELECT 
                    b.customer_id,
                    SUM(b.total) AS totalSpent,
                    COUNT(b.id) AS orderCount,
                    MAX(b.date_time) AS lastOrderDate
                FROM bills b
                WHERE b.date_time BETWEEN :startDate AND :endDate
                GROUP BY b.customer_id
            ) AS bill_totals ON c.id = bill_totals.customer_id
            LEFT JOIN (
                SELECT 
                    b.customer_id,
                    SUM(bi.quantity) AS totalQuantityPurchased
                FROM bills b
                JOIN bill_items bi ON b.id = bi.bill_id
                WHERE b.date_time BETWEEN :startDate AND :endDate
                GROUP BY b.customer_id
            ) AS items_totals ON c.id = items_totals.customer_id
            WHERE COALESCE(bill_totals.orderCount, 0) > 0
            ORDER BY totalSpent DESC
            """, nativeQuery = true)
    List<Object[]> findCustomerReports(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);


    @Query(value = """
        SELECT 
            CASE 
                WHEN :period = 'day' THEN DATE_FORMAT(b.created_at, '%Y-%m-%d')
                WHEN :period = 'week' THEN DATE_FORMAT(b.created_at, '%x-%v')
                WHEN :period = 'month' THEN DATE_FORMAT(b.created_at, '%Y-%m')
                WHEN :period = 'quarter' THEN CONCAT(YEAR(b.created_at), '-Q', QUARTER(b.created_at))
                WHEN :period = 'year' THEN DATE_FORMAT(b.created_at, '%Y')
            END AS period,
            
            -- New customers count
            SUM(CASE WHEN c.created_at BETWEEN :startDate AND :endDate THEN 1 ELSE 0 END) AS newCustomers,
            
            -- Returning customers count
            SUM(CASE WHEN c.created_at < :startDate THEN 1 ELSE 0 END) AS returningCustomers,
            
            COUNT(b.id) AS totalOrders,
            SUM(b.total_amount) AS totalRevenue
        
        FROM bills b
        JOIN customers c ON c.id = b.customer_id
        
        WHERE b.created_at BETWEEN :startDate AND :endDate
        
        GROUP BY period
        ORDER BY period ASC
    """, nativeQuery = true)
    List<Object[]> findCustomerActivityTrends(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate,
                                              @Param("period") String period);

    @Query(value = """
        SELECT 
            i.id AS itemId,
            i.name AS itemName,
            SUM(bi.quantity) AS totalQuantitySold,
            SUM(bi.quantity * bi.price) AS totalRevenue,
            COUNT(DISTINCT b.id) AS orderCount
        FROM bills b
        JOIN bill_items bi ON b.id = bi.bill_id
        JOIN items i ON i.id = bi.item_id
        WHERE b.created_at BETWEEN :startDate AND :endDate
        GROUP BY i.id, i.name
        ORDER BY totalRevenue DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> findTopPerformingItems(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("limit") Integer limit);

    @Query(value = """
        SELECT 
            COALESCE(SUM(bi.subtotal), 0) AS totalRevenue,
            COUNT(DISTINCT b.id) AS totalOrders,
            COALESCE(SUM(bi.quantity), 0) AS totalQuantitySold,
            COUNT(DISTINCT b.customer_id) AS totalCustomers
        FROM bills b
        JOIN bill_items bi ON b.id = bi.bill_id
        WHERE b.date_time BETWEEN :startDate AND :endDate
    """, nativeQuery = true)
    Object findReportSummary(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    @Query(value = """
        SELECT 
            c.id AS customerId,
            c.name AS customerName,
            COALESCE(SUM(b.total_amount), 0) AS totalSpent,
            COUNT(DISTINCT(b.id)) AS orderCount,
            MAX(b.date_time) AS lastOrderDate,
            CASE WHEN COUNT(DISTINCT(b.id)) > 0 
                 THEN COALESCE(SUM(b.total_amount) / COUNT(DISTINCT(b.id)), 0) 
                 ELSE 0 END AS averageOrderValue,
            COALESCE(SUM(bi.quantity), 0) AS totalQuantityPurchased
        FROM customers c
        LEFT JOIN bills b ON c.id = b.customer_id 
            AND b.date_time BETWEEN :startDate AND :endDate
        LEFT JOIN bill_items bi ON b.id = bi.bill_id
        WHERE c.id = :customerId
        GROUP BY c.id, c.name
    """, nativeQuery = true)
    List<Object[]> findCustomerReportById(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("customerId") Integer customerId);
}
