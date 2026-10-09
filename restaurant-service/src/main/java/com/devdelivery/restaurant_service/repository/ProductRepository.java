package com.devdelivery.restaurant_service.repository;

import com.devdelivery.restaurant_service.entity.Product;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByRestaurantId(Integer restaurantId, Pageable pageable);

    @Override
    @NonNull
    Page<Product> findAll(@NonNull Pageable pageable);

    Optional<Product> findByIdAndRestaurantId(Integer id, Integer restaurantId);
}
