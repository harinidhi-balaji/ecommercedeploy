package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    public Order createOrderFromCart(User user) {
        List<Cart> cartItems = cartService.getCartItems(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        for (Cart cartItem : cartItems) {
            if (!productService.hasStock(cartItem.getProduct().getId(), cartItem.getQuantity())) {
                throw new RuntimeException("Insufficient stock for product: " + cartItem.getProduct().getName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());

            productService.reduceStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(user);

        return savedOrder;
    }

    public Order confirmOrder(Long orderId) {
        Order order = findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(Order.OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> findByIdWithItems(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            // Force load the order items to avoid lazy loading issues
            order.getOrderItems().size();
            return Optional.of(order);
        }
        return Optional.empty();
    }

    public List<Order> findByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> findAll() {
        return orderRepository.findAllOrderByOrderDateDesc();
    }

    public List<Order> findByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    public Long getOrderCountByUser(User user) {
        return orderRepository.countByUser(user);
    }

    public Double getTotalRevenue() {
        Double revenue = orderRepository.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    public Double getTotalSpentByUser(User user) {
        Double spent = orderRepository.getTotalSpentByUser(user);
        return spent != null ? spent : 0.0;
    }

    public void cancelOrder(Long orderId) {
        Order order = findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == Order.OrderStatus.SHIPPED ||
            order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel order that has been shipped or delivered");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            productService.addStock(orderItem.getProduct().getId(), orderItem.getQuantity());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        Order order = findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderItemRepository.findByOrder(order);
    }

    public List<Object[]> getMostSoldProducts() {
        return orderItemRepository.findMostSoldProducts();
    }

    public Long getProductSalesCount(Long productId) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return orderItemRepository.countByProduct(product);
    }
}