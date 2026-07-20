package za.co.pollinate.order_management.service;

import za.co.pollinate.order_management.dto.ProductDTO;
import za.co.pollinate.order_management.model.Product;
import za.co.pollinate.order_management.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Long createProduct(ProductDTO product) {
        Product newProduct = new Product();
        newProduct.setName(product.getName());
        newProduct.setPrice(product.getPrice());
        return productRepository.save(newProduct).getId();
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice()))
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice()))
                .collect(Collectors.toList());
    }
}