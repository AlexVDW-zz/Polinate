package za.co.pollinate.order_management.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductResponse implements Serializable {
    private Long orderId;
    private String message; 
}
