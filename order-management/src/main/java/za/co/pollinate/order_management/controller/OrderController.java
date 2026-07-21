package za.co.pollinate.order_management.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import za.co.pollinate.order_management.dto.OrderDTO;
import za.co.pollinate.order_management.model.Product;
import za.co.pollinate.order_management.dto.CreateOrderResponse;
import za.co.pollinate.order_management.dto.CreateOrderRequest;
import za.co.pollinate.order_management.service.OrderService;
import org.springframework.http.ResponseEntity;
import za.co.pollinate.order_management.dto.ErrorResponse;
import za.co.pollinate.order_management.dto.BaseResponse;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@Tag(name = "Order Controller", description = "APIs for managing orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create a new order", description = "Creates a new order in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateOrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @CacheEvict(value = "allOrders", key = "'all-orders'")
    @PostMapping("/create-order")
    public ResponseEntity<BaseResponse<CreateOrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        log.info("Received request to create order: {}", createOrderRequest);
        Long orderId = orderService.createOrder(createOrderRequest);
        String message = "Order created successfully!";
        CreateOrderResponse response = new CreateOrderResponse(orderId, message);
        BaseResponse<CreateOrderResponse> baseResponse = new BaseResponse<>(null, response);
            
        log.info("Order created successfully with ID: {}", orderId);
        return ResponseEntity.ok(baseResponse);
    }

    @Operation(summary = "Get order by ID", description = "Retrieves an order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/get-order/{id}")
    @Cacheable(value = "orders", key = "#id") 
    public ResponseEntity<BaseResponse<OrderDTO>> getOrder(@PathVariable Long id) {
        log.info("Received request to get order with ID: {}", id);
        OrderDTO orderDTO = orderService.getOrderById(id);

        BaseResponse<OrderDTO> baseResponse = new BaseResponse<>(null, orderDTO);
        log.info("Order retrieved successfully with ID: {}", id);
        return ResponseEntity.ok(baseResponse);
    }

    @Operation(summary = "List all orders", description = "Retrieves a list of all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/list-orders")
    @Cacheable(value = "allOrders", key = "'all-orders'")
    public ResponseEntity<BaseResponse<List<OrderDTO>>> getAllOrders(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to list all orders");
        Pageable pageable = PageRequest.of(page, size); 

        List<OrderDTO> orders = orderService.getAllOrders(pageable);
        BaseResponse<List<OrderDTO>> baseResponse = new BaseResponse<>(null, orders);
        log.info("Orders listed successfully");
        return ResponseEntity.ok(baseResponse);
    }
}