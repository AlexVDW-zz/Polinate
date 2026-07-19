import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {

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
    public CreateProductResponse createProduct(ProductDTO productDTO) {
        // Simulate product creation logic
        Long productId = 1L; // Replace with actual product ID
        String message = "Product created successfully!";
        return new CreateProductResponse(productId, message);
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
    public ProductDTO getProduct(@PathVariable Long id) {
        return new ProductDTO();
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
    public List<ProductDTO> getAllProducts() {
        return Arrays.asList(new ProductDTO());
    }
}