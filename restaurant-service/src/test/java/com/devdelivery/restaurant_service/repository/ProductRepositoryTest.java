package com.devdelivery.restaurant_service.repository;

import com.devdelivery.restaurant_service.entity.Product;
import com.devdelivery.restaurant_service.entity.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar e buscar produtos pelo ID do restaurante")
    public void shouldSaveAndFindProductsByRestaurantId() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Xis da Serra");
        restaurant.setDescription("Lanches tradicionais");
        restaurant.setAddress("Av. Júlio de Castilhos, 1000");
        restaurant.setOwnerId("owner-123");
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        Product product = new Product();
        product.setName("Xis Salada");
        product.setDescription("Com bastante maionese");
        product.setValue(28.00);
        product.setActive(true);
        product.setRestaurant(savedRestaurant);
        productRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> result = productRepository.findByRestaurantId(savedRestaurant.getId(), pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());

        Product foundProduct = result.getContent().get(0);
        assertEquals("Xis Salada", foundProduct.getName());
        assertEquals(savedRestaurant.getId(), foundProduct.getRestaurant().getId());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando o restaurante não possui produtos")
    public void shouldReturnEmptyPageWhenRestaurantHasNoProducts() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Restaurante Sem Produtos");
        restaurant.setDescription("Descrição");
        restaurant.setAddress("Rua A");
        restaurant.setOwnerId("owner-123");
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findByRestaurantId(savedRestaurant.getId(), pageable);

        assertTrue(products.isEmpty());
        assertEquals(0, products.getTotalElements());
    }

    @Test
    @DisplayName("Deve retornar página vazia quando o restaurante não existir")
    public void shouldReturnEmptyPageWhenRestaurantDoesNotExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> products = productRepository.findByRestaurantId(999, pageable);

        assertTrue(products.isEmpty());
        assertEquals(0, products.getTotalElements());
    }

    @Test
    @DisplayName("Deve buscar produto pelo ID e ID do restaurante com sucesso")
    public void shouldFindByIdAndRestaurantIdSuccessfully() {
        Restaurant restaurant = createAndSaveRestaurant("Pizzaria");
        Product product = createAndSaveProduct("Pizza", restaurant);

        entityManager.flush();
        entityManager.clear();

        Optional<Product> foundProduct = productRepository.findByIdAndRestaurantId(product.getId(), restaurant.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals("Pizza", foundProduct.get().getName());
    }

    @Test
    @DisplayName("Deve retornar vazio quando o produto pertencer a outro restaurante")
    public void shouldReturnEmptyWhenProductBelongsToAnotherRestaurant() {
        Restaurant restaurant1 = createAndSaveRestaurant("Pizzaria A");
        Restaurant restaurant2 = createAndSaveRestaurant("Pizzaria B");
        Product product = createAndSaveProduct("Pizza Pepperoni", restaurant1);

        entityManager.flush();
        entityManager.clear();

        Optional<Product> foundProduct = productRepository.findByIdAndRestaurantId(product.getId(), restaurant2.getId());

        assertTrue(foundProduct.isEmpty());
    }

    @Test
    @DisplayName("Deve excluir produto com sucesso")
    public void shouldDeleteProductSuccessfully() {
        Restaurant restaurant = createAndSaveRestaurant("Hamburgueria");
        Product product = createAndSaveProduct("Burger Clássico", restaurant);

        productRepository.delete(product);

        entityManager.flush();
        entityManager.clear();

        Optional<Product> deletedProduct = productRepository.findById(product.getId());
        assertTrue(deletedProduct.isEmpty());
    }

    private Restaurant createAndSaveRestaurant(String name) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setDescription("Descrição teste");
        restaurant.setAddress("Endereço teste");
        restaurant.setOwnerId("owner-123");
        return restaurantRepository.save(restaurant);
    }

    private Product createAndSaveProduct(String name, Restaurant restaurant) {
        Product product = new Product();
        product.setName(name);
        product.setDescription("Descrição produto");
        product.setValue(25.0);
        product.setActive(true);
        product.setRestaurant(restaurant);
        return productRepository.save(product);
    }
}