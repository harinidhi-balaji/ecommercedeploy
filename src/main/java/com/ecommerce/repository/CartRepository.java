package com.ecommerce.repository;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUser(User user);

    Optional<Cart> findByUserAndProduct(User user, Product product);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user = :user")
    void deleteByUser(@Param("user") User user);

    @Query("SELECT SUM(c.quantity * c.product.price) FROM Cart c WHERE c.user = :user")
    Double calculateTotalAmount(@Param("user") User user);

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.user = :user")
    Long countByUser(@Param("user") User user);

    boolean existsByUserAndProduct(User user, Product product);
}