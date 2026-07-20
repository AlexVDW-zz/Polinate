package za.co.pollinate.order_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import za.co.pollinate.order_management.dto.ProductDTO;

import java.util.List;

import za.co.pollinate.order_management.dto.CreateProductResponse;
import za.co.pollinate.order_management.dto.ErrorResponse;
import za.co.pollinate.order_management.service.ProductServiceImpl;

import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import za.co.pollinate.order_management.dto.BaseResponse;
import za.co.pollinate.order_management.dto.CreateProductRequest;

@RestController
@Slf4j
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {
    private final ProductServiceImpl productService;

    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product", description = "Creates a new product in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/create-product")
    public ResponseEntity<BaseResponse<CreateProductResponse>> createProduct(@Valid CreateProductRequest createProductRequest) {
        try {
            log.info("Received request to create product: {}", createProductRequest);
            Long productId = productService.createProduct(createProductRequest.getName(), createProductRequest.getPrice());
            String message = "Product created successfully!";
            CreateProductResponse response = new CreateProductResponse(productId, message);
            BaseResponse<CreateProductResponse> baseResponse = new BaseResponse<>(null, response);
            log.info("Product created successfully with ID: {}", productId);
            return ResponseEntity.ok(baseResponse);
        } catch (RuntimeException e) {
            log.error("Error occurred while creating order", e);
            ErrorResponse errorResponse = new ErrorResponse("PRODUCT_CREATION_FAILED", e.getMessage());
            BaseResponse<CreateProductResponse> baseResponse = new BaseResponse<>(errorResponse, null);
            return ResponseEntity.status(500).body(baseResponse);
        }
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/get-product/{id}")
    public ResponseEntity<BaseResponse<ProductDTO>> getProduct(@PathVariable Long id) {
        try {
            log.info("Received request to lookup product using id: {}", id);
            
            ProductDTO productDTO = productService.getProductById(id);
            
            BaseResponse<ProductDTO> baseResponse = new BaseResponse<>(null, productDTO);
            
            log.info("Successfully retrieved product with id {} : {}", id, productDTO);
            return ResponseEntity.ok(baseResponse);
        } catch (RuntimeException e) {
                 log.error("Error occurred while retrieving all orders", e);
            ErrorResponse errorResponse = new ErrorResponse("PRODUCT_NOT_FOUND", e.getMessage());
            BaseResponse<ProductDTO> baseResponse = new BaseResponse<>(errorResponse, null);
            return ResponseEntity.status(404).body(baseResponse);
        }
    }

    @Operation(summary = "List all products", description = "Retrieves a list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/list-products")
    public ResponseEntity<BaseResponse<List<ProductDTO>>> getAllProducts() {
        try {
            log.info("Received request to lookup all products");
            
            List<ProductDTO> products = productService.getAllProducts();
            BaseResponse<List<ProductDTO>> baseResponse = new BaseResponse<>(null, products);
            
            log.info("Successfully retrieved all products");
            return ResponseEntity.ok(baseResponse);
        } catch (RuntimeException e) {
            log.error("Error occurred while retrieving all orders", e);
            ErrorResponse errorResponse = new ErrorResponse("PRODUCT_LIST_RETRIEVAL_FAILED", e.getMessage());
            BaseResponse<List<ProductDTO>> baseResponse = new BaseResponse<>(errorResponse, null);
            return ResponseEntity.status(500).body(baseResponse);
        }
    }
}