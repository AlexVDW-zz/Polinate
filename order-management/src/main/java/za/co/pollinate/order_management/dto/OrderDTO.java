package za.co.pollinate.order_management.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {
    private Long id;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}
