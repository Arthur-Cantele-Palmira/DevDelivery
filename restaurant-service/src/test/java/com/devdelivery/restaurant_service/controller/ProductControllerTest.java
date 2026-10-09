package com.devdelivery.restaurant_service.controller;

import com.devdelivery.restaurant_service.config.SecurityConfig;
import com.devdelivery.restaurant_service.dto.ProductRequestDTO;
import com.devdelivery.restaurant_service.dto.ProductResponseDTO;
import com.devdelivery.restaurant_service.exception.BusinessRuleException;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
import com.devdelivery.restaurant_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProductController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private ProductRequestDTO createValidDTO() {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("Xis da Casa");
        dto.setDescription("Carne, queijo, salada e maionese especial");
        dto.setValue(28.50);
        dto.setActive(true);
        return dto;
    }

    private ProductResponseDTO createValidResponseDTO(Integer productId, Integer restaurantId) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(productId);
        dto.setName("Xis da Casa");
        dto.setDescription("Carne, queijo, salada e maionese especial");
        dto.setValue(28.50);
        dto.setActive(true);
        dto.setRestaurantId(restaurantId);
        return dto;
    }

    //-- GET ALL --
    @Test
    @DisplayName("GET /restaurants/{id}/products deve retornar 200 listando produtos de um restaurante")
    public void shouldReturnSuccessWhenGettingProducts() throws Exception {
        Integer restaurantId = 1;

        when(productService.findAll(eq(restaurantId), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/restaurants/{restaurantId}/products", restaurantId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("GET /restaurants/{id}/products deve retornar 404 restaurante não existe")
    public void shouldReturnNotFoundWhenRestaurantDoesNotExistOnGetAll() throws Exception {
        Integer restaurantId = 99;

        when(productService.findAll(eq(restaurantId), any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));

        mockMvc.perform(get("/restaurants/{restaurantId}/products", restaurantId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    //-- GET POR ID --
    @Test
    @DisplayName("GET /restaurants/{id}/products/{id} deve retornar 200 com os atributos de um produto")
    public void shouldReturnSuccessWhenGettingProduct() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;
        ProductResponseDTO responseDTO = createValidResponseDTO(productId, restaurantId);

        when(productService.findById(restaurantId, productId)).thenReturn(responseDTO);

        mockMvc.perform(get("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Xis da Casa"));
    }

    @Test
    @DisplayName("GET /restaurants/{id}/products/{id} deve retornar 404 produto não encontrado")
    public void shouldReturnNotFoundWhenProductDoesNotExistsOnGet() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;

        when(productService.findById(restaurantId, productId))
                .thenThrow(new ResourceNotFoundException("Product with id " + productId + " not found"));

        mockMvc.perform(get("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    @DisplayName("GET /restaurants/{id}/products/{id} deve retornar 400 produto pertence a outro restaurante")
    public void shouldReturnBadRequestWhenProductBelongsToAnotherRestaurantOnGet() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;

        when(productService.findById(restaurantId, productId))
                .thenThrow(new BusinessRuleException("Product does not belong to restaurant"));

        mockMvc.perform(get("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    //-- POST --
    @Test
    @DisplayName("POST /restaurants/{id}/products deve retornar 201 para criação de um produto")
    public void shouldReturnSuccessWhenPostingProduct() throws Exception {
        Integer restaurantId = 1;
        ProductRequestDTO requestDTO = createValidDTO();
        ProductResponseDTO responseDTO = createValidResponseDTO(10, restaurantId);

        when(productService.create(eq(restaurantId), any(ProductRequestDTO.class), anyString())).thenReturn(responseDTO);

        mockMvc.perform(post("/restaurants/{restaurantId}/products", restaurantId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("POST /restaurants/{id}/products deve retornar 404 para um restaurante que não existe")
    public void shouldReturnNotFoundWhenRestaurantDoesNotExistOnPost() throws Exception {
        Integer restaurantId = 1;
        ProductRequestDTO requestDTO = createValidDTO();

        when(productService.create(eq(restaurantId), any(ProductRequestDTO.class), anyString()))
                .thenThrow(new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));

        mockMvc.perform(post("/restaurants/{restaurantId}/products", restaurantId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    //-- PUT --
    @Test
    @DisplayName("PUT /restaurants/{id}/products/{id} deve retornar 200 para uma atualização")
    public void shouldReturnSuccessWhenUpdatingProduct() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;
        ProductRequestDTO requestDTO = createValidDTO();
        ProductResponseDTO responseDTO = createValidResponseDTO(productId, restaurantId);

        when(productService.update(any(ProductRequestDTO.class), eq(restaurantId), eq(productId), anyString()))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId));
    }

    @Test
    @DisplayName("PUT /restaurants/{id}/products/{id} deve retornar 404 quando o produto não existe")
    public void shouldReturnNotFoundWhenProductDoesNotExistOnPut() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;
        ProductRequestDTO requestDTO = createValidDTO();

        when(productService.update(any(ProductRequestDTO.class), eq(restaurantId), eq(productId), anyString()))
                .thenThrow(new ResourceNotFoundException("Product with id " + productId + " not found"));

        mockMvc.perform(put("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    @DisplayName("PUT /restaurants/{id}/products/{id} deve retornar 400 quando o produto pertence a outro restaurante")
    public void shouldReturnBadRequestWhenProductBelongsToAnotherRestaurantOnPut() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;
        ProductRequestDTO requestDTO = createValidDTO();

        when(productService.update(any(ProductRequestDTO.class), eq(restaurantId), eq(productId), anyString()))
                .thenThrow(new BusinessRuleException("Product does not belong to restaurant"));

        mockMvc.perform(put("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    //-- DELETE --
    @Test
    @DisplayName("DELETE /restaurants/{id}/products/{id} deve retornar 204 para deletar produto")
    public void shouldReturnSuccessWhenDeletingProduct() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;

        doNothing().when(productService).delete(eq(productId), eq(restaurantId), anyString());

        mockMvc.perform(delete("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /restaurants/{id}/products/{id} deve retornar 404 produto não existe")
    public void shouldReturnNotFoundWhenProductDoesNotExistOnDelete() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;

        doThrow(new ResourceNotFoundException("Product with id " + productId + " not found"))
                .when(productService).delete(eq(productId), eq(restaurantId), anyString());

        mockMvc.perform(delete("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    @DisplayName("DELETE /restaurants/{id}/products/{id} deve retornar 400 produto não pertence ao restaurante")
    public void shouldReturnBadRequestWhenProductBelongsToAnotherRestaurantOnDelete() throws Exception {
        Integer restaurantId = 1;
        Integer productId = 10;

        doThrow(new BusinessRuleException("Product does not belong to restaurant"))
                .when(productService).delete(eq(productId), eq(restaurantId), anyString());

        mockMvc.perform(delete("/restaurants/{restaurantId}/products/{id}", restaurantId, productId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }
}