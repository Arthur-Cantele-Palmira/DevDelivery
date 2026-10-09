package com.devdelivery.restaurant_service.service;

import com.devdelivery.restaurant_service.dto.ProductRequestDTO;
import com.devdelivery.restaurant_service.dto.ProductResponseDTO;
import com.devdelivery.restaurant_service.entity.Product;
import com.devdelivery.restaurant_service.entity.Restaurant;
import com.devdelivery.restaurant_service.exception.BusinessRuleException;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
import com.devdelivery.restaurant_service.repository.ProductRepository;
import com.devdelivery.restaurant_service.repository.RestaurantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private final String userId = "user-123";

    @Test
    @DisplayName("Deve retornar lista de produtos quando buscar todos")
    public void shouldReturnProductListWhenFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        Restaurant restaurant = createValidRestaurant(1, userId);
        Product product = createValidProduct(1, "Produto", 24.00, restaurant);

        Page<Product> productPage = new PageImpl<>(List.of(product), pageable, 1);

        when(restaurantRepository.existsById(1)).thenReturn(true);
        when(productRepository.findByRestaurantId(1, pageable)).thenReturn(productPage);

        Page<ProductResponseDTO> result = productService.findAll(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Produto", result.getContent().getFirst().getName());
        assertEquals(24.00, result.getContent().getFirst().getValue());

        verify(restaurantRepository, times(1)).existsById(1);
        verify(productRepository, times(1)).findByRestaurantId(1, pageable);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existir ao buscar produtos")
    public void shouldThrowResourceNotFoundExceptionWhenFindAllProductsAndRestaurantDoesNotExist() {
        Integer restaurantId = 99;
        Pageable pageable = PageRequest.of(0, 10);

        when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.findAll(restaurantId, pageable));

        verify(productRepository, never()).findByRestaurantId(any(), any());
    }

    @Test
    @DisplayName("Deve retornar produto quando buscar por ID com sucesso")
    public void shouldReturnProductResponseDTOWhenFindByIdSuccess() {
        Restaurant restaurant = createValidRestaurant(1, userId);
        Product product = createValidProduct(1, "Produto", 24.00, restaurant);

        when(restaurantRepository.existsById(1)).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        ProductResponseDTO result = productService.findById(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existir ao buscar produto por ID")
    public void shouldThrowResourceNotFoundExceptionWhenFindProductByIdAndRestaurantDoesNotExist() {
        Integer restaurantId = 99;
        Integer productId = 1;

        when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.findById(restaurantId, productId));

        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException quando produto pertencer a outro restaurante")
    public void shouldThrowBusinessRuleExceptionWhenProductBelongsToAnotherRestaurant() {
        Integer restaurantId = 99;

        Restaurant originalRestaurant = createValidRestaurant(1, userId);
        Product product = createValidProduct(1, "Produto", 24.00, originalRestaurant);

        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThrows(BusinessRuleException.class, () -> productService.findById(restaurantId, 1));
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    public void shouldCreateProductSuccessfully() {
        Integer restaurantId = 1;

        Restaurant restaurant = createValidRestaurant(restaurantId, userId);
        ProductRequestDTO requestDTO = createValidDTO("Produto Bom", 24.00);

        Product savedProduct = createValidProduct(10, requestDTO.getName(), requestDTO.getValue(), restaurant);

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponseDTO result = productService.create(1, requestDTO, userId);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("Produto Bom", result.getName());
        assertEquals(24.00, result.getValue());

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existir ao criar produto")
    public void shouldThrowResourceNotFoundExceptionWhenCreateProductAndRestaurantDoesNotExist() {
        Integer restaurantId = 99;
        ProductRequestDTO requestDTO = createValidDTO("Produto Teste", 15.00);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.create(restaurantId, requestDTO, userId));

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    public void shouldUpdateProductSuccessfully() {
        Integer restaurantId = 1;
        Integer productId = 10;

        Restaurant restaurant = createValidRestaurant(restaurantId, userId);
        Product existingProduct = createValidProduct(productId, "Nome Antigo", 10.00, restaurant);
        ProductRequestDTO requestDTO = createValidDTO("Nome Novo", 25.00);
        Product updatedProduct = createValidProduct(productId, requestDTO.getName(), requestDTO.getValue(), restaurant);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponseDTO result = productService.update(requestDTO, restaurantId, productId, userId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Nome Novo", result.getName());
        assertEquals(25.00, result.getValue());

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existir ao atualizar produto")
    public void shouldThrowResourceNotFoundExceptionWhenUpdateProductAndRestaurantDoesNotExist() {
        Integer restaurantId = 99;
        Integer productId = 1;
        ProductRequestDTO requestDTO = createValidDTO("Produto Atualizado", 20.00);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.update(requestDTO, restaurantId, productId, userId));

        verify(productRepository, never()).save(any());
    }

    // -- DELETE --
    @Test
    @DisplayName("Deve excluir produto com sucesso")
    public void shouldDeleteProductSuccessfully() {
        Integer restaurantId = 1;
        Integer productId = 10;

        Restaurant restaurant = createValidRestaurant(restaurantId, userId);
        Product product = createValidProduct(productId, "Produto", 20.00, restaurant);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        assertDoesNotThrow(() -> productService.delete(productId, restaurantId, userId));

        verify(productRepository, times(1)).findById(productId);
        verifyNoInteractions(restaurantRepository);
    }

    @Test
    @DisplayName("Deve lançar BusinessRuleException ao excluir produto de outro restaurante")
    public void shouldThrowBusinessRuleExceptionWhenDeletingProductFromAnotherRestaurant() {
        Integer restaurantId = 1;
        Integer productId = 10;

        Restaurant otherRestaurant = createValidRestaurant(99, userId);
        Product product = createValidProduct(productId, "Produto", 20.00, otherRestaurant);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(BusinessRuleException.class, () -> productService.delete(productId, restaurantId, userId));

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).delete(any());
        verifyNoInteractions(restaurantRepository);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando produto não existir ao excluir")
    public void shouldThrowResourceNotFoundExceptionWhenDeleteProductAndRestaurantDoesNotExist() {
        Integer restaurantId = 1;
        Integer productId = 10;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.delete(productId, restaurantId, userId));

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).delete(any());
        verifyNoInteractions(restaurantRepository);
    }

    private Restaurant createValidRestaurant(Integer id, String ownerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName("Restaurante Teste");
        restaurant.setDescription("Descrição");
        restaurant.setAddress("Endereço");
        restaurant.setOwnerId(ownerId);
        return restaurant;
    }

    private Product createValidProduct(Integer id, String name, Double value, Restaurant restaurant) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Descrição Produto");
        product.setValue(value);
        product.setActive(true);
        product.setRestaurant(restaurant);
        return product;
    }

    private ProductRequestDTO createValidDTO(String name, Double value) {
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName(name);
        dto.setDescription("Descrição do produto");
        dto.setValue(value);
        dto.setActive(true);
        return dto;
    }
}