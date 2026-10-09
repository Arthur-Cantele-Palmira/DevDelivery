package com.devdelivery.restaurant_service.repository;

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
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar e buscar restaurante por ID com sucesso")
    public void shouldSaveAndFindRestaurantById() {
        Restaurant restaurant = createRestaurant("Xis da Serra", "Lanches tradicionais", "Av. Júlio de Castilhos, 1000", "owner-123");
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        entityManager.flush();
        entityManager.clear();

        Optional<Restaurant> foundRestaurant = restaurantRepository.findById(savedRestaurant.getId());

        assertTrue(foundRestaurant.isPresent());
        assertEquals("Xis da Serra", foundRestaurant.get().getName());
        assertEquals("owner-123", foundRestaurant.get().getOwnerId());
    }

    @Test
    @DisplayName("Deve retornar todos os restaurantes paginados")
    public void shouldReturnAllRestaurantsPaged() {
        Restaurant restaurant1 = createRestaurant("Pizzaria A", "Pizzas", "Rua A", "owner-1");
        Restaurant restaurant2 = createRestaurant("Hamburgueria B", "Lanches", "Rua B", "owner-2");

        restaurantRepository.save(restaurant1);
        restaurantRepository.save(restaurant2);

        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Restaurant> result = restaurantRepository.findAll(pageable);

        assertFalse(result.isEmpty());
        assertTrue(result.getTotalElements() >= 2);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro se o restaurante existir por ID")
    public void shouldReturnTrueWhenRestaurantExistsById() {
        Restaurant restaurant = createRestaurant("Restaurante Teste", "Descrição", "Endereço", "owner-123");
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        entityManager.flush();
        entityManager.clear();

        boolean exists = restaurantRepository.existsById(savedRestaurant.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve retornar falso se o restaurante não existir por ID")
    public void shouldReturnFalseWhenRestaurantDoesNotExistById() {
        boolean exists = restaurantRepository.existsById(999);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve deletar restaurante com sucesso")
    public void shouldDeleteRestaurantSuccessfully() {
        Restaurant restaurant = createRestaurant("Restaurante Para Deletar", "Descrição", "Endereço", "owner-123");
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        restaurantRepository.delete(savedRestaurant);

        entityManager.flush();
        entityManager.clear();

        Optional<Restaurant> deleted = restaurantRepository.findById(savedRestaurant.getId());
        assertTrue(deleted.isEmpty());
    }

    private Restaurant createRestaurant(String name, String description, String address, String ownerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setDescription(description);
        restaurant.setAddress(address);
        restaurant.setOwnerId(ownerId);
        return restaurant;
    }
}