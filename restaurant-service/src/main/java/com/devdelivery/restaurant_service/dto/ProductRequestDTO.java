package com.devdelivery.restaurant_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados para criação ou atualização de produtos")
public class ProductRequestDTO {
    @NotBlank(message = "Name can´t be blank.")
    @Size(min = 3, max = 50, message = "Name need to be between 3 and 50.")
    @Schema(description = "Nome do produto", example = "Hámburguer", minLength = 3, maxLength = 50)
    private String name;

    @NotBlank(message = "Description can´t be blank.")
    @Size(min = 3, max = 200, message = "Description need to be between 3 and 200.")
    @Schema(description = "Descrição do produto", example="300g de carne bovina...", minLength=3, maxLength=200)
    private String description;

    @NotNull(message = "Value can´t be blank.")
    @Positive(message = "Value can´t be less than 0.")
    @Schema(description = "Valor do produto", example="35,90")
    private Double value;

    @NotNull(message = "Active status is required.")
    @Schema(description = "Indica se o produto está disponível ou não")
    private Boolean active;
}
