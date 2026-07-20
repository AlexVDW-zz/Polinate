package za.co.pollinate.order_management.service;

import za.co.pollinate.order_management.dto.ProductDTO;
import java.util.List;
import java.math.BigDecimal;

public interface ProductService {
    Long createProduct(String name, BigDecimal price);
    ProductDTO getProductById(Long id);
    List<ProductDTO> getAllProducts();
}