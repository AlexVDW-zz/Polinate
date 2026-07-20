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
import za.co.pollinate.order_management.exception.NotFoundException;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public Long createOrder(CreateOrderRequest request) {
        Order newOrder = new Order();

        List<OrderItem> orderItems = request.getCartItems().stream()
                .map(itemDTO -> {
                    OrderItem orderItem = new OrderItem();
                    
                    Product product = productRepository.findById(itemDTO.getProductId()).orElse(null);
                    if(product == null) {
                        throw new NotFoundException("Product not found with id: " + itemDTO.getProductId());
                    }

                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemDTO.getQuantity());

                    return orderItem;
                })
                .collect(Collectors.toList());

        BigDecimal totalPrice = orderItems.stream()
                .map(item -> {
                    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                    return item.getProduct().getPrice().multiply(quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        newOrder.setOrderItems(orderItems);
        newOrder.setTotalPrice(totalPrice);
        return orderRepository.save(newOrder).getId();
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
        .map(order -> {
            List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDTO(
                            new ProductDTO(orderItem.getProduct().getId(), orderItem.getProduct().getName(), orderItem.getProduct().getPrice()),
                            orderItem.getQuantity()
                    ))
                    .collect(Collectors.toList());
            return new OrderDTO(order.getId(), orderItemDTOs, order.getTotalPrice(), order.getCreatedAt());
        })
        .orElseThrow(() -> new NotFoundException("Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                            .map(orderItem -> new OrderItemDTO(
                                    new ProductDTO(orderItem.getProduct().getId(), orderItem.getProduct().getName(), orderItem.getProduct().getPrice()),
                                    orderItem.getQuantity()
                            ))
                            .collect(Collectors.toList());

                    return new OrderDTO(order.getId(), orderItemDTOs, order.getTotalPrice(), order.getCreatedAt());
                })
                .collect(Collectors.toList());
    }
}