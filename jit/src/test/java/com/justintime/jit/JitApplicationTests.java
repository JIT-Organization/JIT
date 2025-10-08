package com.justintime.jit;

import com.justintime.jit.bean.JwtBean;
import com.justintime.jit.repository.OrderRepo.OrderRepository;
import com.justintime.jit.service.*;
import com.justintime.jit.service.impl.OrderServiceImpl;
import com.justintime.jit.util.CommonServiceImplUtil;
import jakarta.persistence.EntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * An abstract base class for service layer unit tests.
 * This class sets up the common mocking infrastructure needed for testing services.
 * It uses JUnit 5 and Mockito.
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public abstract class JitApplicationTests {

		// Use @InjectMocks to create an instance of the class and inject the mocks into it.
		@InjectMocks
		protected OrderServiceImpl orderService;

		// Use @Mock to create mock implementations for the dependencies of OrderServiceImpl.
		@Mock
		protected OrderRepository orderRepository;
		@Mock
		protected RestaurantService restaurantService;
		@Mock
		protected ReservationService reservationService;
		@Mock
		protected UserService userService;
		@Mock
		protected OrderItemService orderItemService;
		@Mock
		protected PaymentService paymentService;
		@Mock
		protected CommonServiceImplUtil commonServiceImplUtil;
		@Mock
		protected ApplicationEventPublisher eventPublisher;
		@Mock
		protected EntityManager entityManager;

		// A real JwtBean instance is used as it's a simple POJO.
		protected JwtBean jwtBean;

		/**
		 * This method runs before each test.
		 * It sets up the JwtBean and injects it into the OrderServiceImpl instance.
		 * The JwtBean is not a Spring-managed bean that can be injected via the constructor,
		 * so we use ReflectionTestUtils to set this private field.
		 */
		@BeforeEach
		void setUp() {
			jwtBean = new JwtBean();
			// Set default values for the JWT bean for our tests.
			jwtBean.setRestaurantCode("TGSR");
			jwtBean.setUsername("testuser@example.com");

			// Manually inject the jwtBean into the BaseServiceImpl parent class of OrderServiceImpl
			ReflectionTestUtils.setField(orderService, "jwtBean", jwtBean);
		}

		/**
		 * A helper utility to read and parse a JSON file from the test resources folder.
		 * This is useful for creating complex test objects from a file.
		 * @param stream The InputStream of the JSON file (e.g., from getResourceAsStream).
		 * @param clazz The class to map the JSON to.
		 * @return An object of type T populated with data from the JSON file.
		 * @throws IOException if the file cannot be read or parsed.
		 */
		public static <T> T readJsonFromFile(InputStream stream, Class<T> clazz) throws IOException {
			if (stream == null) {
				throw new IOException("InputStream cannot be null. Check if the file exists in resources.");
			}
			BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
			ObjectMapper objectMapper = new ObjectMapper();
			// Configure ObjectMapper for modern Java types like LocalDateTime
			objectMapper.registerModule(new JavaTimeModule());
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper.readValue(bufferedInputStream, clazz);
		}
}
