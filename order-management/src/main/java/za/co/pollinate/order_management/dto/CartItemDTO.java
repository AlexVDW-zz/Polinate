package za.co.pollinate.order_management.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO implements Serializable {
    private Long productId;
    private Integer quantity;
}
