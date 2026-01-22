package com.tata.Retail_product_order.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tata.Retail_product_order.controller.ProductController;
import com.tata.Retail_product_order.dto.ProductDTO;
import com.tata.Retail_product_order.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfig.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductDTO product;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )

                .build();

        product = new ProductDTO(
                1L,
                "Laptop",
                "Gaming laptop",
                new BigDecimal("1200.00"),
                10,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }




    @Test
    @WithMockUser
    void getAllProducts_success() throws Exception {

        Page<ProductDTO> page =
                new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);

        when(productService.getAllProducts(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_success_whenAdmin() throws Exception {

        when(productService.createProduct(any(ProductDTO.class)))
                .thenReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_success() throws Exception {

        when(productService.updateProduct(eq(1L), any(ProductDTO.class)))
                .thenReturn(product);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_success() throws Exception {

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}