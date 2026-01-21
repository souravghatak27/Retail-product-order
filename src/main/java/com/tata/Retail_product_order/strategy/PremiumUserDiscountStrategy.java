package com.tata.Retail_product_order.strategy;

import com.tata.Retail_product_order.entity.UserRole;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("premiumUserDiscountStrategy")
public class PremiumUserDiscountStrategy implements DiscountStrategy {
    
    private static final BigDecimal PREMIUM_DISCOUNT_RATE = new BigDecimal("0.10");

    @Override
    public boolean isApplicable(UserRole role, BigDecimal orderTotal) {
        return role == UserRole.PREMIUM_USER;
    }
    @Override
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        return orderTotal.multiply(PREMIUM_DISCOUNT_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public String getDescription() {
        return "10% premium user discount";
    }
}
