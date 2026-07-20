package za.co.pollinate.order_management.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO implements Serializable {
    private ProductDTO product;
    private Integer quantity;
}
