package com.tata.Retail_product_order.dto;



import com.tata.Retail_product_order.entity.UserRole;

import java.math.BigDecimal;

public record DiscountRequest(BigDecimal orderTotal, BigDecimal itemTotal, Integer quantity, String couponCode,
                              UserRole customerType) {

}
