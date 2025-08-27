package com.charith.pahanaedu.repository;

import com.charith.pahanaedu.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByAccountNumber(String accountNumber);
    Optional<Customer> findByAccountNumber(String accountNumber);

    @Query("SELECT c FROM Customer c WHERE " +
            "c.name LIKE %:keyword% OR " +
            "c.telephone LIKE %:keyword% OR " +
            "c.accountNumber LIKE %:keyword%")
    List<Customer> findByNameOrTelephoneOrAccountNumber(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.units = :units WHERE c.id = :customerId")
    void updateUnits(@Param("customerId") Integer customerId, @Param("units") Integer units);
}
