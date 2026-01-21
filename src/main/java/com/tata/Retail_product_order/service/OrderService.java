package com.tata.Retail_product_order.service;


import com.tata.Retail_product_order.dto.OrderDTO;
import com.tata.Retail_product_order.dto.OrderItemDTO;
import com.tata.Retail_product_order.dto.OrderRequest;
import com.tata.Retail_product_order.entity.*;
import com.tata.Retail_product_order.exception.InsufficientStockException;
import com.tata.Retail_product_order.exception.ResourceNotFoundException;
import com.tata.Retail_product_order.repository.OrderRepository;
import com.tata.Retail_product_order.repository.ProductRepository;
import com.tata.Retail_product_order.repository.UserRepository;
import com.tata.Retail_product_order.strategy.DiscountCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final UserRepository userRepository;

    private final DiscountCalculator discountCalculator;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, DiscountCalculator discountCalculator) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.discountCalculator = discountCalculator;
    }
    
    @Transactional
    public OrderDTO createOrder(OrderRequest request) {
        log.info("Creating new order");
        
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Validate stock availability for all items
        validateStock(request);
        
        // Create order
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .build();
        
        BigDecimal subtotal = BigDecimal.ZERO;
        
        // Process each order item
        for (var itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.productId()));
            
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            subtotal = subtotal.add(itemTotal);
            
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.getPrice())
                    .discountApplied(BigDecimal.ZERO)
                    .totalPrice(itemTotal)
                    .build();
            
            order.addItem(orderItem);
            
            // Decrease product stock
            product.setQuantity(product.getQuantity() - itemRequest.quantity());
            productRepository.save(product);
        }
        
        
        BigDecimal totalDiscount = discountCalculator.calculateDiscount(user.getRole(), subtotal);
        BigDecimal orderTotal = subtotal.subtract(totalDiscount);
        
        
        if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
            for (OrderItem item : order.getItems()) {
                BigDecimal itemDiscount = totalDiscount
                        .multiply(item.getTotalPrice())
                        .divide(subtotal, 2, BigDecimal.ROUND_HALF_UP);
                
                item.setDiscountApplied(itemDiscount);
                item.setTotalPrice(item.getTotalPrice().subtract(itemDiscount));
            }
        }
        
        order.setOrderTotal(orderTotal);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}", savedOrder.getId());
        
        return mapToDTO(savedOrder);
    }
    
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        log.debug("Fetching order with id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        
        // Check if user has permission to view this order
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        if (!order.getUser().getUsername().equals(username) && 
            !authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new ResourceNotFoundException("Order not found with id: " + id);
        }
        
        return mapToDTO(order);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDTO> getMyOrders(Pageable pageable) {
        log.debug("Fetching orders for current user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        return orderRepository.findByUser(user, pageable)
                .map(this::mapToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        log.debug("Fetching all orders");
        return orderRepository.findAll(pageable)
                .map(this::mapToDTO);
    }
    
    private void validateStock(OrderRequest request) {
        for (var itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.productId()));
            
            if (product.getDeleted()) {
                throw new ResourceNotFoundException("Product not found with id: " + itemRequest.productId());
            }
            
            if (product.getQuantity() < itemRequest.quantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName() + 
                        ". Available: " + product.getQuantity() + 
                        ", Requested: " + itemRequest.quantity()
                );
            }
        }
    }
    
    private OrderDTO mapToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> new OrderItemDTO(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getDiscountApplied(),
                        item.getTotalPrice()
                ))
                .toList();
        
        return new OrderDTO(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                itemDTOs,
                order.getOrderTotal(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
