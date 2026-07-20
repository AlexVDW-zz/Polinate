package za.co.pollinate.order_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import za.co.pollinate.order_management.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
