package com.devdelivery.restaurant_service.repository;

import com.devdelivery.restaurant_service.entity.Category;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Override
    @NonNull
    Page<Category> findAll(@NonNull Pageable pageable);
}
