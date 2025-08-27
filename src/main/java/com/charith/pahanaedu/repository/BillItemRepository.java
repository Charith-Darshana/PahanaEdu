package com.charith.pahanaedu.repository;

import com.charith.pahanaedu.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Integer> {
    List<BillItem> findByBillId(Integer billId);

    @Query("SELECT COUNT(bi) FROM BillItem bi WHERE bi.bill.customer.id = :customerId")
    Integer countItemsByCustomerId(@Param("customerId") Integer customerId);

    @Query("""
            SELECT SUM(bi.subtotal) AS totalRevenue, SUM(bi.quantity) AS totalSold, MAX(b.dateTime) AS lastSoldDate
            FROM BillItem bi
            INNER JOIN Bill b ON b.id = bi.bill.id
            INNER JOIN Item i ON i.id = bi.item.id
            WHERE i.id = :itemId
        """)
    Object[] findLessQuantityItemStats(@Param("itemId") Integer itemId);
}
