package za.co.pollinate.order_management.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest implements Serializable {
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    private BigDecimal price;
}
