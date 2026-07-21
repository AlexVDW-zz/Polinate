package za.co.pollinate.order_management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.argThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import za.co.pollinate.order_management.dto.ProductDTO;
import za.co.pollinate.order_management.exception.NotFoundException;
import za.co.pollinate.order_management.model.Product;
import za.co.pollinate.order_management.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct_savesAndReturnsGeneratedId() {
        final String name = "";
        final BigDecimal price = new BigDecimal("9.99");

        Product saved = new Product();
        saved.setId(99L);

        when(productRepository.save(any(Product.class)))
            .thenReturn(saved);

        Long id = productService.createProduct(name, price);

        verify(productRepository).save(argThat(x -> {
            return x.getName().equals(name)
                    && x.getPrice().equals(price);
        }));

        assertThat(id).isEqualTo(saved.getId());
    }

    @Test
    void getProductById_returnsDtoWhenFound() {
        Product product = new Product(1L, "Test", new BigDecimal("9.99"));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductDTO dto = productService.getProductById(product.getId());

        assertThat(dto.getId()).isEqualTo(product.getId());
        assertThat(dto.getName()).isEqualTo(product.getName());
        assertThat(dto.getPrice()).isEqualByComparingTo(product.getPrice());
    }

    @Test
    void getProductById_throwsWhenNotFound() {
        final Long id = 99L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAllProducts_mapsEntitiesToDtos() {
        Product p1 = new Product(1L, "Test1", new BigDecimal("9.99"));
        Product p2 = new Product(2L, "Test2", new BigDecimal("19.99"));
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductDTO> products = productService.getAllProducts();

        assertThat(products).hasSize(2)
                .extracting(ProductDTO::getName)
                .containsExactly(p1.getName(), p2.getName());
    }
}
