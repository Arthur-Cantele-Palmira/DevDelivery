package com.devdelivery.restaurant_service.service;

import com.devdelivery.restaurant_service.dto.RestaurantRequestDTO;
import com.devdelivery.restaurant_service.dto.RestaurantResponseDTO;
import com.devdelivery.restaurant_service.entity.Restaurant;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
import com.devdelivery.restaurant_service.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RestaurantService {
    @Autowired
    private final RestaurantRepository restaurantRepository;

    public Page<RestaurantResponseDTO> findAll(Pageable pageable) {
        Page<Restaurant> restaurantsPage = restaurantRepository.findAll(pageable);
        return restaurantsPage.map(RestaurantResponseDTO::new);
    }

    public RestaurantResponseDTO findById(Integer id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

        return convertToDTO(restaurant);
    }

    @Transactional
    public RestaurantResponseDTO create(RestaurantRequestDTO requestDTO, String ownerId) {
        Restaurant restaurant = convertToEntity(requestDTO);
        restaurant.setOwnerId(ownerId);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return convertToDTO(savedRestaurant);
    }

    @Transactional
    public RestaurantResponseDTO update(Integer id, RestaurantRequestDTO requestDTO, String currentUserId) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

        if (!restaurant.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission to update this restaurant");
        }

        updateEntityFromDTO(requestDTO, restaurant);

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);

        return convertToDTO(updatedRestaurant);
    }

    @Transactional
    public void delete(Integer id, String ownerId) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + id + " not found"));

        if(!restaurant.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("You do not have permission to update this restaurant");
        }

        restaurantRepository.deleteById(id);
    }

    public RestaurantResponseDTO convertToDTO(Restaurant restaurant) {
        RestaurantResponseDTO dto = new RestaurantResponseDTO();

        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setDescription(restaurant.getDescription());
        dto.setAddress(restaurant.getAddress());

        return dto;
    }

    private void updateEntityFromDTO(RestaurantRequestDTO dto, Restaurant entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setAddress(dto.getAddress());
    }

    public Restaurant convertToEntity(RestaurantRequestDTO dto) {
        Restaurant restaurant = new Restaurant();

        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setAddress(dto.getAddress());

        return restaurant;
    }
}
