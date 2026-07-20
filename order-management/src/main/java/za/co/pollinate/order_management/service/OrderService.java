package za.co.pollinate.order_management.service;

import org.springframework.stereotype.Service;
import za.co.pollinate.order_management.model.OrderItem;
import za.co.pollinate.order_management.model.Order;
import za.co.pollinate.order_management.model.Product;
import za.co.pollinate.order_management.dto.OrderDTO;
import za.co.pollinate.order_management.dto.OrderItemDTO;
import za.co.pollinate.order_management.dto.ProductDTO;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import za.co.pollinate.order_management.repository.OrderRepository;
import za.co.pollinate.order_management.repository.ProductRepository;

import org.springframework.transaction.annotation.Transactional;

import za.co.pollinate.order_management.dto.CreateOrderRequest;

public interface OrderService {
    Long createOrder(CreateOrderRequest request);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getAllOrders();
}