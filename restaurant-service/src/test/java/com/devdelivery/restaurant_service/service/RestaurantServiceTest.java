package com.devdelivery.restaurant_service.service;

import com.devdelivery.restaurant_service.dto.RestaurantRequestDTO;
import com.devdelivery.restaurant_service.dto.RestaurantResponseDTO;
import com.devdelivery.restaurant_service.entity.Restaurant;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
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
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private final String userId = "user-123";

    @Test
    @DisplayName("Deve retornar lista de restaurantes quando buscar todos")
    public void shouldReturnRestaurantListWhenFindAll() {
        Pageable pageable = PageRequest.of(0, 10);

        Restaurant restaurant = createValidRestaurant(1, "Restaurante", "Restaurante de comida italiana", "Rua n° 0");

        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(restaurant), pageable, 1);

        when(restaurantRepository.findAll(pageable)).thenReturn(restaurantPage);

        Page<RestaurantResponseDTO> result = restaurantService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Restaurante", result.getContent().getFirst().getName());

        verify(restaurantRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar restaurante quando buscar por ID com sucesso")
    public void shouldReturnRestaurantResponseDTOWhenFindByIdSuccess() {
        Restaurant restaurant = createValidRestaurant(1, "Restaurante", "Restaurante de comida italiana", "Rua n° 0");

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        RestaurantResponseDTO result = restaurantService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Restaurante", result.getName());
        assertEquals("Restaurante de comida italiana", result.getDescription());
        assertEquals("Rua n° 0", result.getAddress());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não for encontrado pelo ID")
    public void shouldThrowResourceNotFoundExceptionWhenFindByIdFails() {
        Integer restaurantId = 1;
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.findById(restaurantId));
    }

    @Test
    @DisplayName("Deve criar restaurante com sucesso")
    public void shouldCreateRestaurantSuccessfully() {
        RestaurantRequestDTO dto = createValidDTO();

        Restaurant restaurant = createValidRestaurant(1, dto.getName(), dto.getDescription(), dto.getAddress());

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);

        RestaurantResponseDTO result = restaurantService.create(dto, userId);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Restaurante", result.getName());
        assertEquals("Restaurante de comida italiana", result.getDescription());
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve atualizar restaurante com sucesso")
    public void shouldUpdateRestaurantSuccessfully() {
        RestaurantRequestDTO dto = createValidDTO();
        Restaurant existingRestaurant = createValidRestaurant(1, "Antigo Nome", "Descrição antiga", "Rua Antiga");
        Restaurant updatedRestaurant = createValidRestaurant(1, dto.getName(), dto.getDescription(), dto.getAddress());

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(existingRestaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(updatedRestaurant);

        RestaurantResponseDTO result = restaurantService.update(1, dto, userId);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Restaurante", result.getName());
        assertEquals("Restaurante de comida italiana", result.getDescription());
        assertEquals("Rua n° 0", result.getAddress());
        verify(restaurantRepository, times(1)).findById(1);
        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existir ao atualizar")
    public void shouldThrowResourceNotFoundExceptionWhenUpdateFails() {
        Integer restaurantId = 1;
        RestaurantRequestDTO dto = createValidDTO();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.update(restaurantId, dto, userId));

        verify(restaurantRepository, times(1)).findById(restaurantId);
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve excluir restaurante com sucesso")
    public void shouldDeleteRestaurantSuccessfully() {
        Restaurant restaurant = createValidRestaurant(1, "Restaurante", "Descrição", "Endereço");

        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        doNothing().when(restaurantRepository).deleteById(1);

        assertDoesNotThrow(() -> restaurantService.delete(1, userId));

        verify(restaurantRepository, times(1)).findById(1);
        verify(restaurantRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando restaurante não existir ao excluir")
    public void shouldThrowResourceNotFoundExceptionWhenDeleteFails() {
        when(restaurantRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> restaurantService.delete(1, userId));

        verify(restaurantRepository, times(1)).findById(1);
        verify(restaurantRepository, never()).deleteById(any());
    }

    private RestaurantRequestDTO createValidDTO() {
        RestaurantRequestDTO dto = new RestaurantRequestDTO();
        dto.setName("Restaurante");
        dto.setDescription("Restaurante de comida italiana");
        dto.setAddress("Rua n° 0");
        return dto;
    }

    private Restaurant createValidRestaurant(Integer id, String name, String description, String address) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setDescription(description);
        restaurant.setAddress(address);
        restaurant.setOwnerId(userId);
        return restaurant;
    }
}