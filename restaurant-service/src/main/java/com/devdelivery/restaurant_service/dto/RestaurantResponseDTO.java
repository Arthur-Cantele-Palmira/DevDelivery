package com.devdelivery.restaurant_service.dto;

import com.devdelivery.restaurant_service.entity.Restaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RestaurantResponseDTO {
    private Integer id;

    @Schema(description = "Nome do restaurante", example = "Donnas")
    private String name;

    @Schema(description = "Descrição do restaurante", example = "Hamburgueria...")
    private String description;

    @Schema(description = "Endereço comercial do restaurante", example = "Av. Rua dos Bobos")
    private String address;

    @Schema(description = "Lista dos horários de funcionamento")
    private List<OperatingHoursResponseDTO> operatingHours;

    public RestaurantResponseDTO() {}

    public RestaurantResponseDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.description = restaurant.getDescription();
        this.address = restaurant.getAddress();

        if(restaurant.getOperatingHours() != null) {
          this.operatingHours = restaurant.getOperatingHours().stream()
                  .map(OperatingHoursResponseDTO::new)
                  .toList();
        }
    }
}
