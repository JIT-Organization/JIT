# JIT
# Project Documentation – REST API Overview

> **Scope**
> This document describes the REST API surface of a Spring MVC application that exposes “read‑by‑id” operations for five domain entities:      
> * **Transaction**
> * **Admin**
> * **ReservationActivity**
> * **Address**
> * **Category**

All endpoints share the same structure, behaviour and error handling strategy. They all return a **200 OK** with the resource when found or a **404 Not Found** when the resource does not exist.

---

## 1. General Design Principles

| Principle | Description |
|-----------|-------------|
| **Statelessness** | Each request is independent. The endpoint does not rely on session state. |
| **Resource–oriented** | CRUD semantics are mapped to HTTP verbs – this section describes the *read* path (`GET`). |
| **Simplicity** | The pattern `GET /{id}` is used for all entities. |
| **Consistency** | The response payload and status codes are identical across all resources. |
| **Optional Wrapper** | Service layer method returns `Optional<T>`, allowing the controller to decide on the HTTP status. |

---

## 2. API Base URL

The base URL depends on the project’s deployment context.
If the application is deployed to `/api`, the full endpoints are:

```
GET /api/transactions/{id}
GET /api/admins/{id}
GET /api/reservation-activities/{id}
GET /api/addresses/{id}
GET /api/categories/{id}
```

> *Note*: The path segment (e.g., `transactions`) matches the entity name in **snake‑case**.
> Adjust the base path as needed for your environment.

---

## 3. Endpoint Glossary

| Entity | HTTP Method | URI | Path Variable | Typical JSON Payload | Status Codes |
|--------|-------------|-----|---------------|----------------------|--------------|
| **Transaction** | GET | `/api/transactions/{id}` | `id` (Long) | ![](JSON payload omitted – see Section 4) | 200 OK (found)<br>404 Not Found (not found) |
| **Admin** | GET | `/api/admins/{id}` | `id` (Long) | ![](JSON payload omitted – see Section 4) | 200 OK<br>404 Not Found |
| **ReservationActivity** | GET | `/api/reservation-activities/{id}` | `id` (Long) | ![](JSON payload omitted – see Section 4) | 200 OK<br>404 Not Found |
| **Address** | GET | `/api/addresses/{id}` | `id` (Long) | ![](JSON payload omitted – see Section 4) | 200 OK<br>404 Not Found |
| **Category** | GET | `/api/categories/{id}` | `id` (Long) | ![](JSON payload omitted – see Section 4) | 200 OK<br>404 Not Found |

### 3.1 Request Example

```http
GET /api/transactions/42 HTTP/1.1
Host: example.com
Accept: application/json
```

### 3.2 Successful Response (200)

```json
{
  "id": 42,
  "amount": 99.99,
  "timestamp": "2025-08-15T10:23:45Z",
  "description": "Payment to DemoCo"
}
```

> *The exact fields depend on the domain model.  See Section 4 for a quick reference.*

### 3.3 Error Response (404)

```http
HTTP/1.1 404 Not Found
Content-Type: application/json

{
  "error": "Resource not found",
  "id": 42
}
```

---

## 4. Domain Models (Quick Reference)

| Entity | Sample Fields |
|--------|---------------|
| **Transaction** | `id: Long`, `amount: BigDecimal`, `timestamp: OffsetDateTime`, `description: String` |
| **Admin** | `id: Long`, `username: String`, `email: String`, `role: String` |
| **ReservationActivity** | `id: Long`, `userId: Long`, `activityStart: OffsetDateTime`, `activityEnd: OffsetDateTime` |
| **Address** | `id: Long`, `street: String`, `city: String`, `country: String`, `postalCode: String` |
| **Category** | `id: Long`, `name: String`, `description: String` |

> *These are illustrative examples.  The actual models may contain additional fields, relationships or validation constraints.*

---

## 5. Common Service Contract

Each controller delegates to a service with the following signature:

```java
Optional<Entity> get<Entity>ById(Long id);
```

The service is responsible for:

1. Querying the underlying repository (DB, cache, etc.).
2. Returning `Optional.empty()` if no record is found.

The controller converts the `Optional` to an appropriate `ResponseEntity`:

```java
return service.get<Entity>ById(id)
              .map(ResponseEntity::ok)
              .orElse(ResponseEntity.notFound().build());
```

---

## 6. Extending the API

If a new entity is added (e.g., **Product**), follow these steps:

1. **Define Model** – create `Product` entity class.
2. **Service** – implement `Optional<Product> getProductById(Long id)`.
3. **Controller** – add:

   ```java
   @GetMapping("/{id}")
   public ResponseEntity<Product> getProductById(@PathVariable Long id) {
       return productService.getProductById(id)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
   }
   ```

4. **Endpoints** – add `/api/products/{id}` to the documentation.
5. **Tests** – add unit/integration tests to verify 200/404 behavior.

---

## 7. Error Handling Strategy

The current API only emits:

* **200 OK** – successful retrieval.
* **404 Not Found** – entity not present.

If you need additional error handling (e.g., **400 Bad Request** for invalid IDs, **500 Internal Server Error** for database issues), you can: 

* Validate `id` before calling the service (e.g., `@Positive`).
* Introduce `@ControllerAdvice` to handle exceptions globally.
* Return structured error bodies consistent with the example in Section 3.3.

---

## 8. Testing Checklist

| Test Case | Expected Outcome |
|-----------|-------------------|
| Existing ID → GET → 200 Response body contains the entity. |
| Non‑existent ID → GET → 404 | Empty body or error JSON. |
| Malformed ID (not a number) → GET → 400 (if validation) | Error JSON with validation message. |
| Service throws exception → GET → 500 | Exception mapping or global error handler. |

---

## 9. Closing Remarks

- The Consistency across resources simplifies client code and reduces learning curves.
- Adding more CRUD operations (POST / PUT / DELETE) follows the same pattern but with different service methods and status codes.

Happy developing!
