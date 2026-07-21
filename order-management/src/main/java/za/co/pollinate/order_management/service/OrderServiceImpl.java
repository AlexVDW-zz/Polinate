package za.co.pollinate.order_management.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import za.co.pollinate.order_management.dto.CreateOrderRequest;
import za.co.pollinate.order_management.dto.OrderDTO;
import za.co.pollinate.order_management.dto.OrderItemDTO;
import za.co.pollinate.order_management.dto.ProductDTO;
import za.co.pollinate.order_management.exception.NotFoundException;
import za.co.pollinate.order_management.model.Order;
import za.co.pollinate.order_management.model.OrderItem;
import za.co.pollinate.order_management.model.Product;
import za.co.pollinate.order_management.repository.OrderRepository;
import za.co.pollinate.order_management.repository.ProductRepository;

@Service
@Slf4j
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
        log.info("Started creating order with inputs: {}", request);    

        Order newOrder = new Order();

        List<OrderItem> orderItems = request.getCartItems().stream()
                .map(itemDTO -> {
                    OrderItem orderItem = new OrderItem();
                    
                    Product product = productRepository.findById(itemDTO.getProductId()).orElse(null);
                    if(product == null) {
                        String errorMessage = "Error while creating order. Product with ID: " + itemDTO.getProductId() + " does not exist.";
                        log.error(errorMessage);    
                        throw new NotFoundException(errorMessage);
                    }

                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemDTO.getQuantity());
                    orderItem.setOrder(newOrder);

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

        orderRepository.save(newOrder);

        log.info("Order created successfully with ID: {}", newOrder.getId());    

        return newOrder.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDTO getOrderById(Long id) {
        log.info("Started order lookup using orderId: {}", id);    

        return orderRepository.findById(id)
        .map(order -> {
            log.info("Found order {id}. Mapping Order Items", id);
            List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDTO(
                            new ProductDTO(orderItem.getProduct().getId(), orderItem.getProduct().getName(), orderItem.getProduct().getPrice()),
                            orderItem.getQuantity()
                    ))
                    .collect(Collectors.toList());
            log.info("Successfully mapped order {}.", id);
            
            OrderDTO orderDTO = new OrderDTO(order.getId(), orderItemDTOs, order.getTotalPrice(), order.getCreatedAt());

            log.info("Successfully looked up order using orderId: {}", id);    

            return orderDTO;
        })
        .orElseThrow(() -> {
            String errorMessage = "Order not found with id: " + id;
            log.error(errorMessage);
            return new NotFoundException(errorMessage);
        });
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> getAllOrders() {
        log.info("Started retrieving all orders");    

        List<OrderDTO> orders = orderRepository.findAll().stream()
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

        log.info("Successfully retrieved all orders");

        return orders;
    }
}