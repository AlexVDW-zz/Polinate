package za.co.pollinate.order_management.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private Number createProduct(String name, String price) throws Exception {
        String requestBody = "{\"name\": \"" + name + "\", \"price\": " + price + "}";
        String response = mockMvc.perform(post("/api/products/create-product")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.data.productId");
    }

    @Test
    void createOrder_persistsOrderWithItemsAndCorrectTotal() throws Exception {
        Number productId = createProduct("Test", "10.00");

        String createOrderBody = "{\"cartItems\": [{\"productId\": " + productId + ", \"quantity\": 3}]}";

        String createResponse = mockMvc.perform(post("/api/orders/create-order")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        Number orderId = JsonPath.read(createResponse, "$.data.orderId");

        mockMvc.perform(get("/api/orders/get-order/{id}", orderId)
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPrice", is(30.0)))
                .andExpect(jsonPath("$.data.orderItems.length()", is(1)))
                .andExpect(jsonPath("$.data.orderItems[0].quantity", is(3)))
                .andExpect(jsonPath("$.data.orderItems[0].product.name", is("Widget")));
    }

    @Test
    void createOrder_rejectedWhenProductMissing() throws Exception {
        String createOrderBody = "{\"cartItems\": [{\"productId\": 999999, \"quantity\": 1}]}";

        mockMvc.perform(post("/api/orders/create-order")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.errorCode", is("NOT_FOUND")))
                .andExpect(jsonPath("$.error.errorMessage", notNullValue()));
    }

    @Test
    void createOrder_rejectedWhenCartIsEmpty() throws Exception {
        String createOrderBody = "{\"cartItems\": []}";

        mockMvc.perform(post("/api/orders/create-order")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.errorCode", is("VALIDATION_ERROR")));
    }

    @Test
    void getOrder_returns404WhenMissing() throws Exception {
        mockMvc.perform(get("/api/orders/get-order/{id}", 999999)
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.errorCode", is("NOT_FOUND")));
    }

    @Test
    void createOrder_requiresAuthentication() throws Exception {
        String requestBody = "{\"name\": \"" + "test" + "\", \"price\": " + 9.99 + "}";
        mockMvc.perform(post("/api/products/create-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                        .andExpect(status().isUnauthorized());
    }

    @Test
    void getOrder_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/orders/get-order/{id}", 1))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listOrders_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/orders/list-orders"))
                .andExpect(status().isUnauthorized());
    }
}
