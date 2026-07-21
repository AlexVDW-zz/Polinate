package za.co.pollinate.order_management.service;

import za.co.pollinate.order_management.dto.OrderDTO;

import java.util.List;
import za.co.pollinate.order_management.dto.CreateOrderRequest;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Long createOrder(CreateOrderRequest request);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getAllOrders(Pageable pageable);
}