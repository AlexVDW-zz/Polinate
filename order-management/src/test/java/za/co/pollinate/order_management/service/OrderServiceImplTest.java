package za.co.pollinate.order_management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import za.co.pollinate.order_management.dto.CartItemDTO;
import za.co.pollinate.order_management.dto.CreateOrderRequest;
import za.co.pollinate.order_management.dto.OrderDTO;
import za.co.pollinate.order_management.exception.NotFoundException;
import za.co.pollinate.order_management.model.Order;
import za.co.pollinate.order_management.model.OrderItem;
import za.co.pollinate.order_management.model.Product;
import za.co.pollinate.order_management.repository.OrderRepository;
import za.co.pollinate.order_management.repository.ProductRepository;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_calculatesTotalPriceAndLinksItemsToOrder() {
        Product product = new Product(1L, "Test", new BigDecimal("10.00"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(100L);
            return order;
        });

        CreateOrderRequest request = new CreateOrderRequest(List.of(new CartItemDTO(1L, 3)));

        Long orderId = orderService.createOrder(request);

        assertThat(orderId).isEqualTo(100L);

        var orderCaptor = org.mockito.ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo("30.00");
        assertThat(savedOrder.getOrderItems()).hasSize(1);
        OrderItem savedItem = savedOrder.getOrderItems().get(0);
        assertThat(savedItem.getOrder()).isSameAs(savedOrder);
        assertThat(savedItem.getProduct()).isEqualTo(product);
        assertThat(savedItem.getQuantity()).isEqualTo(3);
    }

    @Test
    void createOrder_rejectsOrderWhenProductDoesNotExist() {
        final Long id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());
        CreateOrderRequest request = new CreateOrderRequest(List.of(new CartItemDTO(id, 1)));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrderById_returnsDtoWhenFound() {
        Product product = new Product(1L, "Test", new BigDecimal("10.00"));
        Order order = new Order(1L, new BigDecimal("20.00"), null, LocalDateTime.now());
        OrderItem item = new OrderItem(1L, order, product, 2);
        order.setOrderItems(List.of(item));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderDTO dto = orderService.getOrderById(order.getId());

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTotalPrice()).isEqualByComparingTo(order.getTotalPrice());
        assertThat(dto.getOrderItems()).hasSize(1);
        assertThat(dto.getCreatedAt()).isEqualTo(order.getCreatedAt());
        assertThat(dto.getOrderItems().get(0).getQuantity()).isEqualTo(order.getOrderItems().size());
        assertThat(dto.getOrderItems().get(0).getProduct().getName()).isEqualTo(product.getName());
    }

    @Test
    void getOrderById_throwsNotFoundExceptionWhenNotFound() {
        final Long id = 99L;

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
