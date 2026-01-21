package com.tata.Retail_product_order.strategy;

import com.tata.Retail_product_order.entity.UserRole;

import java.math.BigDecimal;

public interface DiscountStrategy {

    boolean isApplicable(UserRole role, BigDecimal orderTotal);

    BigDecimal calculateDiscount(BigDecimal orderTotal);

    String getDescription();
}
