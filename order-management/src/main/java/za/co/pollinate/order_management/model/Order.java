package za.co.pollinate.order_management.model;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "order")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade=CascadeType.ALL)
    private List<OrderItem> orderItems;

    @CreationTimestamp
    @Column(name = "created_at", nullable=false)
    private LocalDateTime createdAt;
}
