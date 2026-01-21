package com.tata.Retail_product_order.exception;

public class InsufficientStockException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InsufficientStockException(String message) {
		super(message);
	}

	public InsufficientStockException(Long productId, Integer requested, Integer available) {
		super("Insufficient stock for productId=" + productId + ", requested=" + requested + ", available="
				+ available);
	}

}
