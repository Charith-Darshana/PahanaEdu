package com.charith.pahanaedu.repository;

import com.charith.pahanaedu.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    @Query("SELECT i FROM Item i WHERE i.name LIKE %:name%")
    List<Item> findByNameContaining(@Param("name") String name);

    @Query("SELECT i FROM Item i WHERE i.quantity < :quantity")
    List<Item> findByQuantityLessThan(@Param("quantity") Integer quantity);

    @Query(value = """
        SELECT 
            i.id AS itemId,
            i.name AS itemName,
            i.description AS description,
            i.quantity AS currentStock,
            COALESCE(SUM(bi.quantity), 0) AS totalSold,
            COALESCE(SUM(bi.subtotal), 0) AS totalRevenue,
            i.price AS unitPrice,
            (i.quantity * i.price) AS stockValue,
            MAX(b.date_time) AS lastSoldDate
        FROM items i
        LEFT JOIN bill_items bi ON i.id = bi.item_id
        LEFT JOIN bills b ON b.id = bi.bill_id 
            AND b.date_time BETWEEN :startDate AND :endDate
        GROUP BY i.id, i.name, i.description, i.quantity, i.price
        ORDER BY i.name ASC
    """, nativeQuery = true)
    List<Object[]> findInventoryReports(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query(value = """
        SELECT 
            i.id AS itemId,
            i.name AS itemName,
            i.description AS description,
            i.quantity AS currentStock,
            COALESCE(SUM(bi.quantity), 0) AS totalSold,
            COALESCE(SUM(bi.subtotal), 0) AS totalRevenue,
            i.price AS unitPrice,
            (i.quantity * i.price) AS stockValue,
            MAX(b.date_time) AS lastSoldDate
        FROM items i
        LEFT JOIN bill_items bi ON i.id = bi.item_id
        LEFT JOIN bills b ON b.id = bi.bill_id 
            AND b.date_time BETWEEN :startDate AND :endDate
        WHERE i.id = :itemId
        GROUP BY i.id, i.name, i.description, i.quantity, i.price
    """, nativeQuery = true)
    List<Object[]> findInventoryReportById(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           @Param("itemId") Integer itemId);
}
