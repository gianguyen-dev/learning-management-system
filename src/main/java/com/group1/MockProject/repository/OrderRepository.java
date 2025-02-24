package com.group1.MockProject.repository;

import com.group1.MockProject.entity.Review;
import com.group1.MockProject.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    double calculateTotalRevenue();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdDate >= :startOfMonth")
    double calculateRevenueForCurrentMonth(@Param("startOfMonth") LocalDateTime startOfMonth);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdDate BETWEEN :startOfLastMonth AND :endOfLastMonth")
    double calculateRevenueForLastMonth(@Param("startOfLastMonth") LocalDateTime start, 
                                      @Param("endOfLastMonth") LocalDateTime end);
} 