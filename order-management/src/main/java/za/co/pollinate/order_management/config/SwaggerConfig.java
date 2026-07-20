package za.co.pollinate.order_management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Order Management API",
        version = "v1.0",
        description = "API for managing orders.",
        contact = @Contact(name = "Alex vd W", email = "support@example.com")
    )
)
public class SwaggerConfig {
}