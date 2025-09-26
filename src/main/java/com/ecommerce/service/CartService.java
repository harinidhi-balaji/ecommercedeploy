package com.ecommerce.service;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    public Cart addToCart(User user, Long productId, Integer quantity) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.hasStock(quantity)) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, product);

        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            int newQuantity = cart.getQuantity() + quantity;

            if (!product.hasStock(newQuantity)) {
                throw new RuntimeException("Insufficient stock for requested quantity");
            }

            cart.setQuantity(newQuantity);
            return cartRepository.save(cart);
        } else {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(quantity);
            return cartRepository.save(cart);
        }
    }

    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    public void removeFromCart(User user, Long productId) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Cart> cart = cartRepository.findByUserAndProduct(user, product);
        cart.ifPresent(cartRepository::delete);
    }

    public Cart updateQuantity(Long cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartRepository.delete(cart);
            return null;
        }

        if (!cart.getProduct().hasStock(quantity)) {
            throw new RuntimeException("Insufficient stock for requested quantity");
        }

        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    public List<Cart> getCartItems(User user) {
        return cartRepository.findByUser(user);
    }

    public BigDecimal getCartTotal(User user) {
        Double total = cartRepository.calculateTotalAmount(user);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    public Long getCartItemCount(User user) {
        return cartRepository.countByUser(user);
    }

    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }

    public boolean isProductInCart(User user, Long productId) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return cartRepository.existsByUserAndProduct(user, product);
    }

    public Optional<Cart> findCartItem(User user, Long productId) {
        Product product = productService.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return cartRepository.findByUserAndProduct(user, product);
    }

    public Cart increaseQuantity(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        int newQuantity = cart.getQuantity() + 1;
        if (!cart.getProduct().hasStock(newQuantity)) {
            throw new RuntimeException("Insufficient stock");
        }

        cart.setQuantity(newQuantity);
        return cartRepository.save(cart);
    }

    public Cart decreaseQuantity(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (cart.getQuantity() <= 1) {
            cartRepository.delete(cart);
            return null;
        }

        cart.setQuantity(cart.getQuantity() - 1);
        return cartRepository.save(cart);
    }
}