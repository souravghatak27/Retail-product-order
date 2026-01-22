package com.tata.Retail_product_order.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.Retail_product_order.controller.OrderController;
import com.tata.Retail_product_order.dto.*;

import com.tata.Retail_product_order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;
    // ---------------------------------------------------
    // CREATE ORDER
    // ---------------------------------------------------

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .build();
    }

    @Test
    void createOrder_success() throws Exception {

        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);
        OrderRequest request =
                new OrderRequest(List.of(itemRequest));

        OrderItemDTO orderItem = new OrderItemDTO(
                1L, 1L, "Laptop", 2,
                new BigDecimal("1299.99"),
                BigDecimal.ZERO,
                new BigDecimal("2599.98")
        );

        OrderDTO response = new OrderDTO(
                1L,
                1L,
                "testuser",
                List.of(orderItem),
                new BigDecimal("2599.98"),
                "PENDING",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderTotal").value(2599.98))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createOrder_invalidInput_badRequest() throws Exception {

        OrderRequest request =
                new OrderRequest(List.of());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_success1() throws Exception {

        OrderDTO order = new OrderDTO(
                1L,
                1L,
                "testuser",
                List.of(),
                new BigDecimal("2599.98"),
                "PENDING",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderTotal").value(2599.98));
    }

}
