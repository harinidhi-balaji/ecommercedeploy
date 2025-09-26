package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    List<Order> findByUserOrderByOrderDateDesc(User user);

    @Query("SELECT o FROM Order o WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") Order.OrderStatus status);

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrderByOrderDateDesc();

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findByOrderDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = :user")
    Long countByUser(@Param("user") User user);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'CONFIRMED'")
    Double getTotalRevenue();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user = :user AND o.status = 'CONFIRMED'")
    Double getTotalSpentByUser(@Param("user") User user);
}