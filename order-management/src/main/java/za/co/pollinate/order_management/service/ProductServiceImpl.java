package za.co.pollinate.order_management.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import za.co.pollinate.order_management.dto.ProductDTO;
import za.co.pollinate.order_management.exception.NotFoundException;
import za.co.pollinate.order_management.model.Product;
import za.co.pollinate.order_management.repository.ProductRepository;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    @Override
    public Long createProduct(String name, BigDecimal price) {
        log.info("Started creating product with name: {}, price: {}", name, price);    

        Product newProduct = new Product();
        newProduct.setName(name);
        newProduct.setPrice(price);
        productRepository.save(newProduct);

        log.info("Product created successfully with ID: {}", newProduct.getId());    

        return newProduct.getId();
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDTO getProductById(Long id) {
        log.info("Started product lookup using id: {}", id);    


        ProductDTO productDTO = productRepository.findById(id)
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice()))
                .orElseThrow(() -> {
                    String errorMessage = "Product with ID: " + id + " does not exist.";
                    log.error(errorMessage); 
                    return new NotFoundException("Product not found with id: " + id);
                    }
                );

            log.info("Successfully looked up product using id: {} - {}", id, productDTO);    

            return productDTO;
        }

    @Transactional(readOnly = true)
    @Override
    public List<ProductDTO> getAllProducts() {
        log.info("Started retrieving all products");

        List<ProductDTO> products = productRepository.findAll().stream()
                .map(product -> new ProductDTO(product.getId(), product.getName(), product.getPrice()))
                .collect(Collectors.toList());

        log.info("Successfully retrieved all products");

        return products;
    }
}