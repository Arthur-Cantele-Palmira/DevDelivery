package com.devdelivery.restaurant_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para a criação de um restaurante")
public class RestaurantRequestDTO {
    @NotBlank(message = "Name can´t be blank.")
    @Size(min = 3, max = 50, message = "Name need to be between 3 and 50.")
    @Schema(description = "Nome do restaurante", example = "Donnas", minLength = 3, maxLength = 50)
    private String name;

    @NotBlank(message = "Description can´t be blank.")
    @Size(min = 3, max = 200, message = "Description need to be between 3 and 200.")
    @Schema(description = "Descrição do restaurante", example = "Hamburgueria...", minLength = 3, maxLength = 200)
    private String description;

    @NotBlank(message = "Address can´t be blank.")
    @Size(min = 3, max = 100, message = "Address need to be between 3 and 100.")
    @Schema(description = "Endereço comercial do restaurante", example = "Av. Rua dos Bobos", minLength = 3, maxLength = 200)
    private String address;
}
