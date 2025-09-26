package com.ecommerce.repository;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByProduct(Product product);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.id = :userId")
    List<OrderItem> findByUserId(@Param("userId") Long userId);

    @Query("SELECT oi.product, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.product ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findMostSoldProducts();

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product = :product")
    Long countByProduct(@Param("product") Product product);
}