package com.devdelivery.restaurant_service.service;

import com.devdelivery.restaurant_service.dto.CategoryRequestDTO;
import com.devdelivery.restaurant_service.dto.CategoryResponseDTO;
import com.devdelivery.restaurant_service.entity.Category;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
import com.devdelivery.restaurant_service.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("Deve retornar lista de categorias quando buscar todas")
    public void shouldReturnCategoryListWhenFindAll() {
        Pageable pageable = PageRequest.of(0,10);

        Category category = new Category();
        category.setId(1);
        category.setName("Sobremesas");

        Page<Category> categoryPage = new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<CategoryResponseDTO> result = categoryService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Sobremesas", result.getContent().getFirst().getName());

        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve retornar categoria quando buscar por ID com sucesso")
    public void shouldReturnCategoryResponseDTOWhenFindByIdSuccess() {
        Integer categoryId = 1;

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Lanches");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        CategoryResponseDTO result = categoryService.findById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Lanches", result.getName());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando categoria não for encontrada pelo ID")
    public void shouldThrowResourceNotFoundExceptionWhenFindByIdFails () {
        Integer categoryId = 99;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, ()->categoryService.findById(categoryId));
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    public void shouldCreateCategorySuccessfully() {
        Integer categoryId = 1;

        CategoryRequestDTO dto = createValidDTO();
        Category category = new Category();
        category.setId(categoryId);
        category.setName(dto.getName());

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.create(dto);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Sobremesas", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    public void shouldUpdateCategorySuccessfully() {
        Integer categoryId = 1;

        CategoryRequestDTO dto = createValidDTO();
        Category category = new Category();
        category.setId(categoryId);
        category.setName(dto.getName());

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponseDTO result = categoryService.update(categoryId, dto);

        assertNotNull(result);
        assertEquals(categoryId, result.getId());
        assertEquals("Sobremesas", result.getName());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando categoria não existir ao atualizar")
    public void shouldThrowResourceNotFoundExceptionWhenUpdateFails() {
        Integer categoryId = 99;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);
        CategoryRequestDTO dto = createValidDTO();

        assertThrows(ResourceNotFoundException.class, ()-> categoryService.update(categoryId, dto));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve excluir categoria com sucesso")
    public void shouldDeleteCategorySuccessfully() {
        Integer categoryId = 1;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(categoryId);

        assertDoesNotThrow(() -> categoryService.delete(categoryId));
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando categoria não existir ao excluir")
    public void shouldThrowResourceNotFoundExceptionWhenDeleteFails() {
        Integer categoryId = 1;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, ()-> categoryService.delete(categoryId));
        verify(categoryRepository, never()).save(any());
    }

    private CategoryRequestDTO createValidDTO() {
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Sobremesas");
        return dto;
    }
}
