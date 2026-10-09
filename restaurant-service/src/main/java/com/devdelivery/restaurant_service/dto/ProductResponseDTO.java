package com.devdelivery.restaurant_service.dto;

import com.devdelivery.restaurant_service.entity.Product;
import com.devdelivery.restaurant_service.entity.Restaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados dos produtos retornados")
public class ProductResponseDTO {
    @Schema(description = "Indentificador único", example = "1")
    private Integer id;

    @Schema(description = "Nome do produto", example = "Hámburguer")
    private String name;

    @Schema(description = "Descrição do produto", example = "300g de carne bovina...")
    private String description;

    @Schema(description = "Valor do produto", example="35,90")
    private Double value;

    @Schema(description = "Indica se o produto está disponível ou não")
    private Boolean active;

    @Schema(description = "Identificador único do restaurante que pertence")
    private Integer restaurantId;

    public ProductResponseDTO() {}

    public ProductResponseDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.value = product.getValue();
        this.active = product.getActive();
        if (product.getRestaurant() != null) {
            this.restaurantId = product.getRestaurant().getId();
        }
    }
}
