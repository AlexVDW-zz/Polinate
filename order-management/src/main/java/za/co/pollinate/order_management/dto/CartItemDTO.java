package za.co.pollinate.order_management.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO implements Serializable {
    @NotNull(message="Product Id is required")
    @Min(value=1, message="Product Id needs to be a positive integer value")
    private Long productId;

    @NotNull(message="Quantity is required")
    @Min(value=1, message="Quantity needs to be a positive integer value")
    private Integer quantity;
}
