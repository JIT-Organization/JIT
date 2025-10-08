package com.justintime.jit;

import com.justintime.jit.dto.OrderDTO;
import com.justintime.jit.entity.Enums.OrderStatus;
import com.justintime.jit.entity.OrderEntities.Order;
import com.justintime.jit.entity.Restaurant;
import com.justintime.jit.entity.User;
import com.justintime.jit.event.OrderCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderTest extends JitApplicationTests {

//    @Test
    @DisplayName("Should Create Order Successfully When Valid Data Is Provided")
    void createOrder_shouldSucceed_whenDataIsValid() throws IOException {
        // --- ARRANGE ---
        // 1. Load test data from a JSON file.
        // This assumes you have a 'create_order_request.json' file in 'src/test/resources/orders/'
        OrderDTO orderDTO = readJsonFromFile(
                OrderTest.class.getResourceAsStream("/orders/create_order_request.json"),
                OrderDTO.class
        );

        // 2. Prepare mock objects that will be returned by our mocked services.
        Restaurant mockRestaurant = new Restaurant(); // Assuming a Restaurant entity class exists
        mockRestaurant.setId(1L);
        mockRestaurant.setRestaurantCode("TGSR");

        User mockUser = new User(); // Assuming a User entity class exists
        mockUser.setId(1L);
        mockUser.setUsername("testuser@example.com");

        Order savedOrder = new Order(); // This is the object we expect the repository to return
        savedOrder.setId(100L);
        savedOrder.setOrderNumber("ORD-2025-100");
        savedOrder.setRestaurant(mockRestaurant);
        savedOrder.setUser(mockUser);

        // 3. Define the behavior of our mocks (stubbing) with SPECIFIC values.
        String expectedRestaurantCode = jwtBean.getRestaurantCode(); // "TGSR"
        String expectedUsername = jwtBean.getUsername();         // "testuser@example.com"

        // Tell the mock to return mockRestaurant ONLY when called with "TGSR".
        when(restaurantService.getRestaurantByRestaurantCode())
                .thenReturn(mockRestaurant);

        // Tell the mock to return mockUser ONLY when called with "TGSR" and "testuser@example.com".
        when(userService.getUserByRestaurantCodeAndUsername(expectedUsername))
                .thenReturn(mockUser);

        // This part remains the same
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When orderItemService is called, return an empty list for simplicity.
//        when(orderItemService.createAndPersistOrderItems(any(OrderDTO.class), anyString(), any(Order.class)))
//                .thenReturn(Collections.emptyList());

        // --- ACT ---
        // Call the method we are testing.
        ResponseEntity<String> response = orderService.createOrder(orderDTO);

        // --- ASSERT ---
        // 1. Verify the response is what we expect.
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Order created successfully with Order Number: ORD-2025-100"));

        // 2. Use ArgumentCaptor to capture the object passed to the save method.
        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);

        // 3. Verify that the mocks were called with the expected parameters.
        // Check that repository's save method was called exactly once.
        verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());

        // Check that eventPublisher's publishEvent was called exactly once.
        verify(eventPublisher, times(1)).publishEvent(any(OrderCreatedEvent.class));

        // Check that entityManager's flush was called exactly once.
        verify(entityManager, times(1)).flush();

        // 4. Inspect the captured argument to ensure the state was set correctly before saving.
        Order capturedOrder = orderArgumentCaptor.getValue();
        assertEquals(OrderStatus.NEW, capturedOrder.getStatus());
        assertEquals("TGSR", capturedOrder.getRestaurant().getRestaurantCode());
        assertEquals("testuser@example.com", capturedOrder.getUser().getUsername());
    }
}
