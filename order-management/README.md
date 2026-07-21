# Order Management

REST API to manage Products and Orders, built with Spring Boot, Spring Data JPA, and an in-memory H2 database.

## How to run the service

Requires Java 21.

```bash
./mvnw spring-boot:run
```

The service starts on `http://localhost:8080`. All endpoints require HTTP Basic authentication:

- Username: `admin`
- Password: `admin`

(configured in `src/main/resources/application.yaml` via `spring.security.user.*`).

Swagger UI: `http://localhost:8080/swagger-ui/index.html` (log in with the credentials mentioned above). 
OpenAPI spec: `http://localhost:8080/v3/api-docs`.

## How to run tests

```bash
./mvnw test
```

This runs:
- **Unit tests** (`src/test/.../service`) - Mockito-based tests for `ProductServiceImpl` and `OrderServiceImpl` covering creation, lookup, the "product not found" rejection path, and total-price calculation.
- **Integration tests** (`src/test/.../controller`) — `@SpringBootTest` + `MockMvc` tests that exercise the real HTTP layer, security, validation, and the H2 database end-to-end (create → persist → retrieve, missing-product rejection, empty-cart rejection, unauthenticated access).

## API overview

### Products (`/api/products`)
- `POST /create-product` - create a product (`name`, `price`)
- `GET /get-product/{id}` - fetch a product by ID
- `GET /list-products` - list all products

### Orders (`/api/orders`)
- `POST /create-order` - create an order from a list of `cartItems` (`productId`, `quantity`). All referenced products must exist; the order is rejected with a `404 NOT_FOUND` error if any product is missing. Total price is calculated using current product prices.
- `GET /get-order/{id}` — fetch an order (with its line items) by ID
- `GET /list-orders` — list all orders

All responses are wrapped in a `BaseResponse` envelope: `{ "data": ..., "error": null }` on success, or `{ "data": null, "error": { "errorCode": ..., "errorMessage": ... } }` on failure.

## Database design

![Order Management DB ERD](design/ERD.png)

- `product` - `name` (varchar), `price` (BigDecimal)
- `orders` - `createdAt` (Timestamp), `total_price` (BigDecimal), stored on the order for fast lookup rather than recalculated from items on every read
- `orderitem` — join table linking an order to a product, with its own `quantity`; this models the many-to-many relationship between `orders` and `product` (one order can contain many products, one product can appear on many orders)

## Assumptions & design notes

- Product prices are captured at order-creation time and baked into the order's `totalPrice`; changing a product's price later does not retroactively change existing orders.
- No update or delete functionality for Order or Product
- An order must contain at least one cart item; empty carts are rejected with a `400 VALIDATION_ERROR`.
- A single shared Basic Auth user (from config) is used for all endpoints, since the spec calls for "a simple authentication mechanism."
- H2 runs in-memory (`jdbc:h2:mem:testdb`) and the schema is recreated on every startup
- Caching added on Orders and Product getById and getAll calls
- Pagination applied only on Orders

