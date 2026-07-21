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
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createGetAndListProduct_endToEnd() throws Exception {
        String requestBody = """
                {"name": "Test", "price": 9.99}
                """;

        String createResponse = mockMvc.perform(post("/api/products/create-product")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        Number productId = JsonPath.read(createResponse, "$.data.productId");

        mockMvc.perform(get("/api/products/get-product/{id}", productId)
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Test")))
                .andExpect(jsonPath("$.data.price", is(9.99)));

        mockMvc.perform(get("/api/products/list-products")
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void getProduct_returns404ErrorWhenMissing() throws Exception {
        mockMvc.perform(get("/api/products/get-product/{id}", 999999)
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.errorCode", is("NOT_FOUND")));
    }

    @Test
    void createProduct_returns400WhenValidationFails() throws Exception {
        String invalidRequestBody = """
                {"name": "", "price": -5}
                """;

        mockMvc.perform(post("/api/products/create-product")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.errorCode", is("VALIDATION_ERROR")));
    }

    @Test
    void endpoints_requireAuthentication() throws Exception {
        mockMvc.perform(get("/api/products/list-products"))
                .andExpect(status().isUnauthorized());
    }
}
