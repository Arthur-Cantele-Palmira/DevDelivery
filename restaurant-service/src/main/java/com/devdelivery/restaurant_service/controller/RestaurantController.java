package com.devdelivery.restaurant_service.controller;

import com.devdelivery.restaurant_service.config.swagger.ApiSecurityResponses;
import com.devdelivery.restaurant_service.dto.RestaurantRequestDTO;
import com.devdelivery.restaurant_service.dto.RestaurantResponseDTO;
import com.devdelivery.restaurant_service.service.RestaurantService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurants")
@CrossOrigin("*")
@AllArgsConstructor
@Tag(name="Restaurants", description="Endpoints para gerenciar restaurantes")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    @Operation(summary = "Listar restaurantes", description = "Retorna uma lista paginada de restaurantes ordenados por nome.")
    @ApiResponse(responseCode="200", description="Lista recuperada")
    @ApiSecurityResponses
    public ResponseEntity<Page<RestaurantResponseDTO>> getRestaurants(@PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(restaurantService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna um restaurante", description = "Retorna os atributos de um restaurante.")
    @ApiResponse(responseCode="200", description="Restaurante recuperado")
    @ApiResponse(responseCode="404", description="Restaurante não encontrado")
    @ApiSecurityResponses
    public ResponseEntity<RestaurantResponseDTO> getRestaurant(@PathVariable Integer id) {
        return ResponseEntity.ok(restaurantService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cria um restaurante", description = "Cria um restaurante baseado no body.")
    @ApiResponse(responseCode="200", description="Restaurante criado")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_RESTAURANT_OWNER', 'SCOPE_ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> postRestaurant(@Valid @RequestBody RestaurantRequestDTO requestDTO, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        RestaurantResponseDTO response = restaurantService.create(requestDTO, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um restaurante", description = "Atualiza os dados do restaurante.")
    @ApiResponse(responseCode="200", description="Restaurante atualizado")
    @ApiResponse(responseCode="404", description="Restaurante não encontrado")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_RESTAURANT_OWNER', 'SCOPE_ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> putRestaurant(@Valid @RequestBody RestaurantRequestDTO requestDTO, @PathVariable Integer id, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        RestaurantResponseDTO response = restaurantService.update(id, requestDTO, ownerId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um restaurante", description = "Deleta um restaurante baseado no id.")
    @ApiResponse(responseCode="200", description="Restaurante deletado")
    @ApiResponse(responseCode="404", description="Restaurante não encontrado")
    @ApiSecurityResponses
    @PreAuthorize("hasAnyAuthority('SCOPE_RESTAURANT_OWNER', 'SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id, @AuthenticationPrincipal Jwt jwt) {
        String ownerId = jwt.getSubject();
        restaurantService.delete(id, ownerId);

        return ResponseEntity.noContent().build();
    }
}
