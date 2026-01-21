package com.tata.Retail_product_order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RetailProductOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetailProductOrderApplication.class, args);
	}


}
