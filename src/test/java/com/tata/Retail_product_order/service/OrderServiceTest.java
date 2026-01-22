package com.tata.Retail_product_order.service;


import com.tata.Retail_product_order.dto.*;
import com.tata.Retail_product_order.entity.*;
import com.tata.Retail_product_order.exception.InsufficientStockException;
import com.tata.Retail_product_order.exception.ResourceNotFoundException;
import com.tata.Retail_product_order.repository.OrderRepository;
import com.tata.Retail_product_order.repository.ProductRepository;
import com.tata.Retail_product_order.repository.UserRepository;
import com.tata.Retail_product_order.strategy.DiscountCalculator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private DiscountCalculator discountCalculator;

	@InjectMocks
	private OrderService orderService;

	private User user;

	private OrderStatus status;

	private Product product;

	@BeforeEach
	void setupSecurity() {
		user = User.builder().id(1L).username("user1").role(UserRole.USER).build();

		product = Product.builder().id(1L).name("Laptop").price(new BigDecimal("1000")).quantity(10).deleted(false)
				.build();

		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("User1", null,
				List.of(new SimpleGrantedAuthority("ROLE_USER"))));
	}

	@AfterEach
	void clearContext() {
		SecurityContextHolder.clearContext();
	}

	// ---------- PLACE ORDER ----------

	@Test
	void createOrder_success() {

		OrderItemDTO item =
				new OrderItemDTO(null, 10L, null, 2, null, null, null);

		OrderRequest request =
				new OrderRequest(List.of(
						new OrderItemRequest(10L, 2)
				));

		when(userRepository.findByUsername("User1"))
				.thenReturn(Optional.of(user));

		when(productRepository.findById(10L))
				.thenReturn(Optional.of(product));

		when(discountCalculator.calculateDiscount(any(), any()))
				.thenReturn(BigDecimal.ZERO);

		when(orderRepository.save(any()))
				.thenAnswer(inv -> inv.getArgument(0));

		OrderDTO response = orderService.createOrder(request);

		assertNotNull(response);
		assertEquals(BigDecimal.valueOf(2000), response.orderTotal());
	}
	@Test
	void shouldThrowExceptionWhenUserNotFound() {

		when(userRepository.findByUsername("User1"))
				.thenReturn(Optional.empty());

		OrderRequest request = new OrderRequest(List.of());

		assertThrows(ResourceNotFoundException.class,
				() -> orderService.createOrder(request));
	}
	@Test
	void placeOrder_insufficientStock() {
		when(userRepository.findByUsername("User1"))
				.thenReturn(Optional.of(user));

		product.setQuantity(1);

		when(productRepository.findById(10L))
				.thenReturn(Optional.of(product));

		OrderRequest request = new OrderRequest(
				List.of(new OrderItemRequest(10L, 5))
		);

		assertThrows(InsufficientStockException.class,
				() -> orderService.createOrder(request));
	}





	@Test
	void getOrderById_forbidden() {
		User otherUser = User.builder().id(2L).username("other").build();

		Order order = Order.builder().id(1L).user(otherUser).items(List.of()).build();

		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(1L));
	}

	// ---------- GET MY ORDERS ----------

	@Test
	void getMyOrders_success() {
		User user = User.builder()
				.id(1L)
				.username("User1")
				.build();

		Order order = Order.builder()
				.id(1L)
				.user(user)
				.items(new ArrayList<>())
				.orderTotal(BigDecimal.TEN)
				.status(OrderStatus.PENDING)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		Authentication auth = mock(Authentication.class);
		when(auth.getName()).thenReturn("User1");

		SecurityContext context = mock(SecurityContext.class);
		when(context.getAuthentication()).thenReturn(auth);
		SecurityContextHolder.setContext(context);

		when(userRepository.findByUsername("User1"))
				.thenReturn(Optional.of(user));

		when(orderRepository.findByUser(eq(user), any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(order)));

		Page<OrderDTO> page =
				orderService.getMyOrders(PageRequest.of(0, 10));

		assertEquals(1, page.getTotalElements());
	}

	// ---------- GET ALL ORDERS ----------

	@Test
	void getAllOrders_success() {
		User user = User.builder()
				.id(1L)
				.username("testuser")
				.build();

		Order order = Order.builder()
				.id(10L)
				.user(user)
				.items(new ArrayList<>())
				.orderTotal(BigDecimal.ONE)
				.status(OrderStatus.PENDING)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		when(orderRepository.findAll(any(Pageable.class)))
				.thenReturn(new PageImpl<>(List.of(order)));

		Page<OrderDTO> page =
				orderService.getAllOrders(PageRequest.of(0, 10));

		assertEquals(1, page.getTotalElements());
	}

	@Test
	void shouldThrowExceptionWhenProductIsDeleted() {

		when(userRepository.findByUsername("User1"))
				.thenReturn(Optional.of(user));

		product.setDeleted(true);

		when(productRepository.findById(10L))
				.thenReturn(Optional.of(product));

		OrderRequest request =
				new OrderRequest(List.of(new OrderItemRequest(10L, 1)));

		assertThrows(ResourceNotFoundException.class,
				() -> orderService.createOrder(request));
	}
}
