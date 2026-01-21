package com.tata.Retail_product_order.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal discountApplied,
        BigDecimal totalPrice
) {
}
