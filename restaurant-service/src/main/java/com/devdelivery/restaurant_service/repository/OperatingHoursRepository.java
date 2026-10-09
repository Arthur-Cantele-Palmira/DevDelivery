package com.devdelivery.restaurant_service.repository;

import com.devdelivery.restaurant_service.entity.OperatingHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatingHoursRepository extends JpaRepository<OperatingHours, Integer> {
}
