package com.tata.Retail_product_order.dto;



import com.tata.Retail_product_order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
		Long id, 
		Long userId, 
		String username, 
		List<OrderItemDTO> items, 
		BigDecimal orderTotal,
		BigDecimal discountApplied, 
		OrderStatus status,
		LocalDateTime createdAt, 
		LocalDateTime updatedAt) 
{
}
