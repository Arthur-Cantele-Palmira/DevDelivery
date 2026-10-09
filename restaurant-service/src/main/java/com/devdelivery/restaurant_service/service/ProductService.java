package com.devdelivery.restaurant_service.service;

import com.devdelivery.restaurant_service.dto.ProductRequestDTO;
import com.devdelivery.restaurant_service.dto.ProductResponseDTO;
import com.devdelivery.restaurant_service.entity.Product;
import com.devdelivery.restaurant_service.entity.Restaurant;
import com.devdelivery.restaurant_service.exception.BusinessRuleException;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
import com.devdelivery.restaurant_service.repository.ProductRepository;
import com.devdelivery.restaurant_service.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProductService {
    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final RestaurantRepository restaurantRepository;

    public Page<ProductResponseDTO> findAll(Integer restaurantId, Pageable pageable) {
        if(!restaurantRepository.existsById(restaurantId)){
            throw new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found");
        }

        Page<Product> productsPage = productRepository.findByRestaurantId(restaurantId, pageable);

        return productsPage.map(ProductResponseDTO::new);
    }

    public ProductResponseDTO findById(Integer restaurantId, Integer id) {
        if(!restaurantRepository.existsById(restaurantId)){
            throw new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        if(!product.getRestaurant().getId().equals(restaurantId)){
            throw new BusinessRuleException("Product with id: " + id + " does not belong to restaurant with id: " + restaurantId);
        }

        return convertToDTO(product);
    }

    @Transactional
    public ProductResponseDTO create(Integer restaurantId, ProductRequestDTO requestDTO, String currentUserId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));

        if(!restaurant.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission to add products to this restaurant");
        }

        Product product = convertToEntity(requestDTO, restaurant);
        Product savedProduct = productRepository.save(product);

        return convertToDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO update(ProductRequestDTO requestDTO, Integer restaurantId, Integer id, String currentUserId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        if(!restaurant.getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission to change products to this restaurant");
        }

        if(!existingProduct.getRestaurant().getId().equals(restaurantId)) {
            throw new BusinessRuleException("Product with id: " + id + " does not belong to restaurant with id: " + restaurantId);
        }

        updateEntityFromDTO(requestDTO, existingProduct);

        Product savedProduct = productRepository.save(existingProduct);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public void delete(Integer id, Integer restaurantId, String currentUserId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        if (!product.getRestaurant().getId().equals(restaurantId)) {
            throw new BusinessRuleException("Product with id: " + id + " does not belong to restaurant with id: " + restaurantId);
        }

        if (!product.getRestaurant().getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("You do not have permission to delete products in this restaurant");
        }

        productRepository.delete(product);
    }

    public ProductResponseDTO convertToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setValue(product.getValue());
        dto.setActive(product.getActive());

        if(product.getRestaurant() != null) {
            dto.setRestaurantId(product.getRestaurant().getId());
        }

        return dto;
    }

    private void updateEntityFromDTO(ProductRequestDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setValue(dto.getValue());
    }

    public Product convertToEntity(ProductRequestDTO dto, Restaurant restaurant) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setValue(dto.getValue());
        product.setActive(dto.getActive());
        product.setRestaurant(restaurant);

        return product;
    }
}
