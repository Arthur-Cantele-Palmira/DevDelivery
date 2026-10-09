package com.devdelivery.restaurant_service.repository;

import com.devdelivery.restaurant_service.entity.Restaurant;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    @Override
    @NonNull
    Page<Restaurant> findAll(@NonNull Pageable pageable);
}
