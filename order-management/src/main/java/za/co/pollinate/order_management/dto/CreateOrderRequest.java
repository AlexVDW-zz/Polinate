package za.co.pollinate.order_management.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest implements Serializable {
    @NotEmpty(message="Order cannot have no cart items linked")
    private List<CartItemDTO> cartItems;
}
