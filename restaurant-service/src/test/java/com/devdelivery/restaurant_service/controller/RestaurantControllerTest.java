package com.devdelivery.restaurant_service.controller;

import com.devdelivery.restaurant_service.config.SecurityConfig;
import com.devdelivery.restaurant_service.dto.OperatingHoursResponseDTO;
import com.devdelivery.restaurant_service.dto.RestaurantRequestDTO;
import com.devdelivery.restaurant_service.dto.RestaurantResponseDTO;
import com.devdelivery.restaurant_service.entity.enums.DayWeek;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
import com.devdelivery.restaurant_service.service.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestaurantController.class)
@AutoConfigureMockMvc
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private RestaurantRequestDTO createValidDTO() {
        RestaurantRequestDTO dto = new RestaurantRequestDTO();
        dto.setName("Xis Caseiro");
        dto.setDescription("Restaurante de Xis");
        dto.setAddress("Av. dos Bobos, N° 0");
        return dto;
    }

    private RestaurantResponseDTO createValidResponseDTO(Integer id) {
        RestaurantResponseDTO dto = new RestaurantResponseDTO();
        dto.setId(id);
        dto.setName("Xis Caseiro");
        dto.setDescription("Restaurante de Xis");
        dto.setAddress("Av. dos Bobos, N° 0");
        return dto;
    }

    //--GET List--
    @Test
    @DisplayName("GET /restaurants deve retornar 200 com lista")
    public void shouldReturnSuccessWhenGettingRestaurants() throws Exception {
        when(restaurantService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/restaurants")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    public void shouldReturnRestaurantWithOperatingHours() throws Exception {
        OperatingHoursResponseDTO hoursDTO = new OperatingHoursResponseDTO();
        hoursDTO.setId(1);
        hoursDTO.setOpenHour(LocalTime.of(18, 0));
        hoursDTO.setCloseHour(LocalTime.of(23, 0));
        hoursDTO.setDay(DayWeek.SATURDAY);

        RestaurantResponseDTO restaurantDTO = new RestaurantResponseDTO();
        restaurantDTO.setId(1);
        restaurantDTO.setName("Pizzaria Pietro");
        restaurantDTO.setOperatingHours(List.of(hoursDTO));

        Page<RestaurantResponseDTO> page = new PageImpl<>(List.of(restaurantDTO));

        when(restaurantService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/restaurants")
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Pizzaria Pietro"))
                .andExpect(jsonPath("$.content[0].operatingHours[0].openHour").value("18:00:00"))
                .andExpect(jsonPath("$.content[0].operatingHours[0].day").value("SATURDAY"));
    }

    //--GET BY ID--
    @Test
    @DisplayName("GET /restaurants/{id} deve retornar 200 com restaurante")
    public void shouldReturnSuccessWhenGettingRestaurant() throws Exception {
        Integer restaurantId = 1;
        RestaurantResponseDTO responseDTO = createValidResponseDTO(restaurantId);

        when(restaurantService.findById(restaurantId)).thenReturn(responseDTO);

        mockMvc.perform(get("/restaurants/{id}", restaurantId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId))
                .andExpect(jsonPath("$.name").value("Xis Caseiro"));
    }

    @Test
    @DisplayName("GET /restaurants/{id} deve retornar 404")
    public void shouldReturnNotFoundWhenRestaurantDoesNotExistOnGet() throws Exception {
        Integer restaurantId = 1;

        when(restaurantService.findById(restaurantId))
                .thenThrow(new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));

        mockMvc.perform(get("/restaurants/{id}", restaurantId)
                        .with(jwt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    //--POST--
    @Test
    @DisplayName("POST /restaurants deve retornar 201 com a criação de um restaurante")
    public void shouldReturnSuccessWhenPostingRestaurant() throws Exception {
        RestaurantRequestDTO requestDTO = createValidDTO();
        RestaurantResponseDTO responseDTO = createValidResponseDTO(1);

        when(restaurantService.create(any(RestaurantRequestDTO.class), anyString())).thenReturn(responseDTO);

        mockMvc.perform(post("/restaurants")
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    //--PUT--
    @Test
    @DisplayName("PUT /restaurants/{id} deve retornar 200 com a atualização de um restaurante")
    public void shouldReturnSuccessWhenUpdatingRestaurant() throws Exception {
        Integer restaurantId = 1;
        RestaurantRequestDTO requestDTO = createValidDTO();
        RestaurantResponseDTO responseDTO = createValidResponseDTO(restaurantId);

        when(restaurantService.update(eq(restaurantId), any(RestaurantRequestDTO.class), anyString())).thenReturn(responseDTO);

        mockMvc.perform(put("/restaurants/{id}", restaurantId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId));
    }

    @Test
    @DisplayName("PUT /restaurants/{id} deve retornar 404")
    public void shouldReturnNotFoundWhenRestaurantDoesNotExistOnPut() throws Exception {
        Integer restaurantId = 1;
        RestaurantRequestDTO requestDTO = createValidDTO();

        when(restaurantService.update(eq(restaurantId), any(RestaurantRequestDTO.class), anyString()))
                .thenThrow(new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"));

        mockMvc.perform(put("/restaurants/{id}", restaurantId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    //--DELETE--
    @Test
    @DisplayName("DELETE /restaurants/{id} deve retornar 204 deletando um restaurante")
    public void shouldReturnSuccessWhenDeletingRestaurant() throws Exception {
        Integer restaurantId = 1;

        doNothing().when(restaurantService).delete(eq(restaurantId), anyString());

        mockMvc.perform(delete("/restaurants/{id}", restaurantId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /restaurants/{id} deve retornar 404")
    public void shouldReturnNotFoundWhenRestaurantDoesNotExistOnDelete() throws Exception {
        Integer restaurantId = 1;

        doThrow(new ResourceNotFoundException("Restaurant with id " + restaurantId + " not found"))
                .when(restaurantService).delete(eq(restaurantId), anyString());

        mockMvc.perform(delete("/restaurants/{id}", restaurantId)
                        .with(jwt().jwt(builder -> builder.subject("user-123")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }
}