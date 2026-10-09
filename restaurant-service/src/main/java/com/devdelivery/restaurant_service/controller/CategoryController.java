package com.devdelivery.restaurant_service.controller;

import com.devdelivery.restaurant_service.config.swagger.ApiSecurityResponses;
import com.devdelivery.restaurant_service.dto.CategoryRequestDTO;
import com.devdelivery.restaurant_service.dto.CategoryResponseDTO;
import com.devdelivery.restaurant_service.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@CrossOrigin("*")
@AllArgsConstructor
@Tag(name="Categories", description="Endpoints para gerenciar categorias")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Retorna uma lista paginada de categorias ordenados por nome.")
    @ApiResponse(responseCode="200", description="Lista recuperada")
    @ApiSecurityResponses
    public ResponseEntity<Page<CategoryResponseDTO>> getCategories(@PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC)Pageable pageable) {
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna uma categoria", description = "Retorna os atributos de uma categoria.")
    @ApiResponse(responseCode="200", description="Categoria recuperada")
    @ApiResponse(responseCode="404", description="Categoria não encontrada")
    @ApiSecurityResponses
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cria uma categoria", description =   "Cria uma categoria baseada no body.")
    @ApiResponse(responseCode="200", description="Categoria criada")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<CategoryResponseDTO> postCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO response = categoryService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary="Atualiza uma categoria", description="Atualiza uma categoria baseada no id.")
    @ApiResponse(responseCode="200", description="Categoria atualizada")
    @ApiResponse(responseCode="400", description ="Categoria não encontrada")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<CategoryResponseDTO> putCategory(@Valid @RequestBody CategoryRequestDTO requestDTO, @PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary="Deleta uma categoria", description="Deleta uma categoria baseada no id.")
    @ApiResponse(responseCode="200", description="Categoria deletada")
    @ApiResponse(responseCode="400", description ="Categoria não encontrada")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
