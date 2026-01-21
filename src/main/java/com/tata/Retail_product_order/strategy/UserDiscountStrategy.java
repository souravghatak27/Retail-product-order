package com.tata.Retail_product_order.strategy;

import com.tata.Retail_product_order.entity.UserRole;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("userDiscountStrategy")
public class UserDiscountStrategy implements DiscountStrategy {

    @Override
    public boolean isApplicable(UserRole role, BigDecimal orderTotal) {
        return role == UserRole.USER || role == UserRole.ADMIN;
    }
    @Override
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        return BigDecimal.ZERO;
    }
    
    @Override
    public String getDescription() {
        return "No discount";
    }
}
