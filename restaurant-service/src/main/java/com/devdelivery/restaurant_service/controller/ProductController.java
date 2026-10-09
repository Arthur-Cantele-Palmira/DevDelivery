package com.devdelivery.restaurant_service.controller;

import com.devdelivery.restaurant_service.config.swagger.ApiSecurityResponses;
import com.devdelivery.restaurant_service.dto.ProductRequestDTO;
import com.devdelivery.restaurant_service.dto.ProductResponseDTO;
import com.devdelivery.restaurant_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants/{restaurantId}/products")
@CrossOrigin("*")
@AllArgsConstructor
@Tag(name="Products", description="Endpoints para gerenciar produtos")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Retorna uma lista paginada de produtos ordenados por nome.")
    @ApiResponse(responseCode="200", description="Lista recuperada")
    @ApiSecurityResponses
    public ResponseEntity<Page<ProductResponseDTO>> getProducts(@PathVariable Integer restaurantId, @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(restaurantId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna um produto", description = "Retorna os atributos de um produto.")
    @ApiResponse(responseCode="200", description="Produto recuperada")
    @ApiResponse(responseCode="404", description="Produto não encontrado")
    @ApiSecurityResponses
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable("id") Integer id, @PathVariable("restaurantId") Integer restaurantId) {
        return ResponseEntity.ok(productService.findById(restaurantId, id));
    }

    @PostMapping
    @Operation(summary = "Cria um produto", description = "Cria um produto baseado no body.")
    @ApiResponse(responseCode="201", description="Produto criado")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_RESTAURANT_OWNER', 'SCOPE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> postProduct(@PathVariable Integer restaurantId,@Valid @RequestBody ProductRequestDTO requestDTO, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        ProductResponseDTO response = productService.create(restaurantId, requestDTO, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um produto", description = "Atualiza um produto baseado no body.")
    @ApiResponse(responseCode="200", description="Produto atualizado")
    @ApiResponse(responseCode="404", description="Produto não encontrado")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_RESTAURANT_OWNER', 'SCOPE_ADMIN')")
    public ResponseEntity<ProductResponseDTO> putProduct(@PathVariable("restaurantId") Integer restaurantId, @PathVariable("id") Integer id, @Valid @RequestBody ProductRequestDTO requestDTO, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        ProductResponseDTO response = productService.update(requestDTO, restaurantId, id,ownerId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um produto", description = "Deleta um produto baseado no id.")
    @ApiResponse(responseCode="200", description="Produto deletado")
    @ApiResponse(responseCode="404", description="Produto não encontrado")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_RESTAURANT_OWNER', 'SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable("restaurantId") Integer restaurantId, @PathVariable("id") Integer id, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        productService.delete(id, restaurantId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
