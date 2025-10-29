# JIT Restaurant Management System - AI Coding Guide

## Project Overview
JIT is a multi-tenant restaurant management system built with Spring Boot 3.5.4 (Java 17), featuring real-time order tracking via WebSocket, JWT authentication with custom permissions, and comprehensive audit logging with Hibernate Envers.

## Architecture Patterns

### Multi-Tenancy via Restaurant Code
- All entities are scoped by `restaurantCode` (String identifier, not FK)
- URL pattern: `/jit-api/{resourceType}/{restaurantCode}/...`
- Use `@AuthenticationPrincipal` with `RestaurantIdResolver` to inject restaurant context
- Example: `OrderController.createOrder(@PathVariable String restaurantCode, ...)`

### Security Architecture
- **JWT Authentication**: `JwtAuthenticationFilter` extracts token → `CustomAuthToken` with embedded `restaurantCode`
- **Custom Permissions**: Use `@PreAuthorize("hasPermission(null, 'PERMISSION_CODE')")` on controller methods
- Permission codes are stored in DB (`Permissions` entity) and evaluated via `CustomPermissionEvaluator`
- Common permissions: `VIEW_ORDERS`, `ADD_ORDERS`, `VIEW_MENU_ITEMS`, `ADD_TABLES`, etc.
- No role-based security—everything uses fine-grained permissions

### Controller-Service-Repository Pattern
- **Controllers**: Extend `BaseController` for validation (`validate()`) and standardized responses (`success()`, `error()`)
- **Services**: Extend `BaseServiceImpl<Entity, ID>` for CRUD operations; use `@Transactional` for write operations
- **Repositories**: Standard Spring Data JPA `JpaRepository` interfaces
- **DTOs**: Use `MapperFactory.getMapper(Entity.class, DTO.class)` for bidirectional mapping via ModelMapper

### Event-Driven Communication
- Use `ApplicationEventPublisher` to publish domain events (e.g., `UserInvitationEvent`, order status changes)
- Listeners use `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` for consistency
- Example: `EmailEventListener` handles async email sending with `@Async` after transaction commit

### WebSocket Real-Time Updates
- STOMP over WebSocket with `/ws` endpoint (no SockJS fallback)
- Destination prefixes: `/jit-api` (client-to-server), `/user` (user-specific), `/topic` (broadcast)
- Authentication via JWT during STOMP CONNECT frame (see `WebSocketSecurityConfig`)
- Use `SimpMessagingTemplate` in services to push updates to `/topic/{restaurantCode}/orders`

### Audit Logging
- **All entities** (except tokens/config) use `@Audited` with Hibernate Envers for automatic revision tracking
- Base entity: `BaseEntity` provides `id`, `createdDttm`, `updatedDttm` via `@CreationTimestamp`/`@UpdateTimestamp`
- Query audit history: use Envers `AuditReader` APIs (not standard JPA)

### Validation Strategy
- Use `ValidationDispatcher` (auto-wired in `BaseController`) to route DTO validation to type-specific validators
- Validators implement `RequestValidator<T>` and support partial validation via `fieldsToValidate` Set
- Call: `validate(dto, Set.of("fieldName"), restaurantCode)` before service layer

## Development Workflows

### Build & Run
```powershell
# Build (Gradle wrapper)
.\gradlew.bat build

# Run locally (requires MySQL, see compose.yaml)
.\gradlew.bat bootRun

# Docker Compose for MySQL
docker compose up mysql
```

### Database Configuration
- **Dev profile**: Uses Aiven MySQL cloud instance (see `application-dev.properties`)
- **Local profile**: Configure MySQL via `compose.yaml` (port 3306, `jit_db` database)
- Hibernate DDL: `spring.jpa.hibernate.ddl-auto=update` (auto-migration, no Flyway)

### Testing
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Bearer token setup: Copy from `application-dev.properties` → `swagger.bearer.token`
- WebSocket testing: Use STOMP client libraries (JavaScript `@stomp/stompjs` recommended)

### Common Gotchas
- **Lombok**: `lombok.config` auto-adds `@SuppressFBWarnings` via `lombok.extern.findbugs.addSuppressFBWarnings=true`
- **CORS**: Frontend runs on `localhost:3000/3001`, configured in `SecurityConfig.corsSource()`
- **Async tasks**: Email sending uses `AsyncConfig` thread pool (`AsyncMail-` prefix, 3-5 threads)
- **SpotBugs warnings**: Suppress with `@SuppressFBWarnings(value = "CODE", justification = "...")`

## Code Conventions

### Entity Design
- All entities extend `BaseEntity` (provides `id`, timestamps)
- Use Lombok: `@Getter`, `@Setter`, `@AllArgsConstructor`, `@NoArgsConstructor`
- Enums live in `entity/Enums/` (e.g., `OrderStatus`, `Role`)
- Complex entities use nested packages: `OrderEntities/`, `ComboEntities/`, `PaymentEntities/`

### Service Layer Patterns
```java
// Mapper initialization (service-level field)
private final GenericMapper<Entity, DTO> mapper = MapperFactory.getMapper(Entity.class, DTO.class);

// Transaction boundaries
@Transactional // Write operations
@Transactional(readOnly = true) // Read-heavy operations

// Event publishing
@Autowired private ApplicationEventPublisher eventPublisher;
eventPublisher.publishEvent(new CustomEvent(...));
```

### Controller Response Pattern
```java
// Success responses
return success(data, "Custom message"); // 200 OK
return success(data); // 200 OK, "Success"

// Error responses  
return error("Error message", HttpStatus.BAD_REQUEST);

// Raw ResponseEntity (avoid unless necessary)
return ResponseEntity.ok(data);
```

### Security Annotations
```java
// Permission-based (standard approach)
@PreAuthorize("hasPermission(null, 'VIEW_ORDERS')")

// Public endpoints (rare, typically auth-related)
// Add to SecurityConfig.filterChain() permitAll list
```

## Key Files Reference
- **Security**: `SecurityConfig`, `JwtAuthenticationFilter`, `CustomPermissionEvaluator`
- **WebSocket**: `WebSocketConfig`, `WebSocketSecurityConfig`, `LoggingChannelInterceptor`
- **Validation**: `ValidationDispatcher`, `BaseController.validate()`
- **Mapping**: `MapperFactory`, `GenericMapper` (in `util/mapper/`)
- **Global Exception Handling**: `GlobalExceptionHandler` (uses `BaseController.error()`)
