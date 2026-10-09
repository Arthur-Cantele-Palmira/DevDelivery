package com.devdelivery.restaurant_service.dto;

import com.devdelivery.restaurant_service.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dados retornados de uma categoria")
public class CategoryResponseDTO {
    @Schema(description = "Identificador único", example="1")
    private Integer id;

    @Schema(description = "Nome da categoria", example="Pizzaria")
    private String name;

    public CategoryResponseDTO() {}

    public CategoryResponseDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}
