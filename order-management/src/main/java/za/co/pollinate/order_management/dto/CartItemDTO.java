package za.co.pollinate.order_management.dto;

import java.io.Serializable;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO implements Serializable {
    @Min(value=1, message="Producy Id needs to be a positive integer value")
    private Long productId;

    @Min(value=1, message="Quantity needs to be a positive integer value")
    private Integer quantity;
}
