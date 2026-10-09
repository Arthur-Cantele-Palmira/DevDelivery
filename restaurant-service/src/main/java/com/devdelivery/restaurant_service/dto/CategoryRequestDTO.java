package com.devdelivery.restaurant_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para criação ou atualização de categorias")
public class CategoryRequestDTO {
    @NotBlank(message = "Name can´t be blank.")
    @Size(min = 3, max = 50, message = "Name need to be between 3 and 50.")
    @Schema(description = "Nome da categoria", example="Pizzaria", minLength=3, maxLength =50)
    private String name;
}
