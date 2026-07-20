package za.co.pollinate.order_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse implements Serializable {
    private Long orderId;
    private String message;
}
