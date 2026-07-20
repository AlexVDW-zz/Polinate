package za.co.pollinate.order_management.service;

import za.co.pollinate.order_management.dto.ProductDTO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.math.BigDecimal;

@Service
public interface ProductService {
    Long createProduct(String name, BigDecimal price);
    ProductDTO getProductById(Long id);
    List<ProductDTO> getAllProducts();
}