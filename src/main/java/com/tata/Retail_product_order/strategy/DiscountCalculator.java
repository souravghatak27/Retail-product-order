package com.tata.Retail_product_order.strategy;


import com.tata.Retail_product_order.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DiscountCalculator {

    private final List<DiscountStrategy> discountStrategies;

    public BigDecimal calculateDiscount(UserRole role, BigDecimal orderTotal) {

        return discountStrategies.stream()
                .filter(strategy -> strategy.isApplicable(role, orderTotal))
                .map(strategy -> strategy.calculateDiscount(orderTotal))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public String getDiscountDescription(UserRole role, BigDecimal orderTotal) {

        List<String> descriptions = discountStrategies.stream()
                .filter(strategy -> strategy.isApplicable(role, orderTotal))
                .map(DiscountStrategy::getDescription)
                .toList();

        return descriptions.isEmpty()
                ? "No discount applied"
                : String.join(" + ", descriptions);
    }
}
