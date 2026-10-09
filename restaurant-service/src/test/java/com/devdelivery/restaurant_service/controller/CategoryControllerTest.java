package com.devdelivery.restaurant_service.controller;

import com.devdelivery.restaurant_service.config.SecurityConfig;
import com.devdelivery.restaurant_service.dto.CategoryRequestDTO;
import com.devdelivery.restaurant_service.dto.CategoryResponseDTO;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
import com.devdelivery.restaurant_service.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CategoryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private CategoryRequestDTO createValidDTO() {
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Xis da Casa");
        return dto;
    }

    private CategoryResponseDTO createValidResponseDTO(Integer id) {
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(id);
        dto.setName("Xis da Casa");
        return dto;
    }

    //--GET List--
    @Test
    @DisplayName("GET /categories deve retornar 200 lista de categorias")
    public void shouldReturnSuccessWhenGettingCategories() throws Exception {
        when(categoryService.findAll(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    //--GET POR ID--
    @Test
    @DisplayName("GET /categories/{id} deve retornar 200 e os atributos de uma categoria")
    public void shouldReturnSuccessWhenGettingCategory() throws Exception {
        Integer categoryId = 1;
        CategoryResponseDTO responseDTO = createValidResponseDTO(categoryId);

        when(categoryService.findById(categoryId)).thenReturn(responseDTO);

        mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Xis da Casa"));
    }

    @Test
    @DisplayName("GET /categories/{id} deve retornar 404")
    public void shouldReturnNotFoundWhenCategoryDoesNotExistsOnGet() throws Exception {
        Integer categoryId = 1;

        when(categoryService.findById(categoryId))
                .thenThrow(new ResourceNotFoundException("Category with id " + categoryId + " not found"));

        mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    //--POST--
    @Test
    @DisplayName("POST /categories deve retornar 201 e criar uma categoria")
    public void shouldReturnSuccessWhenPostingCategory() throws Exception {
        CategoryRequestDTO requestDTO = createValidDTO();
        CategoryResponseDTO responseDTO = createValidResponseDTO(1);

        when(categoryService.create(any(CategoryRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    //--PUT--
    @Test
    @DisplayName("PUT /categories/{id} deve retornar 200 e atualizar a categoria")
    public void shouldReturnSuccessWhenUpdatingCategory() throws Exception {
        Integer categoryId = 1;
        CategoryRequestDTO requestDTO = createValidDTO();
        CategoryResponseDTO responseDTO = createValidResponseDTO(categoryId);

        when(categoryService.update(eq(categoryId), any(CategoryRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /categories/{id} deve retornar 404")
    public void shouldReturnNotFoundWhenCategoryDoesNotExistsOnPut() throws Exception {
        Integer categoryId = 1;
        CategoryRequestDTO requestDTO = createValidDTO();

        when(categoryService.update(eq(categoryId), any(CategoryRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Category with id " + categoryId + " not found"));

        mockMvc.perform(put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    //--DELETE--
    @Test
    @DisplayName("DELETE /categories/{id} deve retornar 200 e deletar a categoria")
    public void shouldReturnSuccessWhenDeletingCategory() throws Exception {
        Integer categoryId = 1;

        doNothing().when(categoryService).delete(categoryId);

        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /categories/{id} deve retornar 404 categoria não encontrada")
    public void shouldReturnNotFoundWhenCategoryDoesNotExistsOnDelete() throws Exception {
        Integer categoryId = 1;

        doThrow(new ResourceNotFoundException("Category with id " + categoryId + " not found"))
                .when(categoryService).delete(categoryId);

        mockMvc.perform(delete("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }
}