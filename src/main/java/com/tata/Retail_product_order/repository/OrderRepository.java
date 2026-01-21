package com.tata.Retail_product_order.repository;


import com.tata.Retail_product_order.entity.Order;
import com.tata.Retail_product_order.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Page<Order> findByUser(User user, Pageable pageable);
    
    Page<Order> findByUserId(Long userId, Pageable pageable);
}
