package com.devdelivery.restaurant_service.service;

import com.devdelivery.restaurant_service.dto.CategoryRequestDTO;
import com.devdelivery.restaurant_service.dto.CategoryResponseDTO;
import com.devdelivery.restaurant_service.entity.Category;
import com.devdelivery.restaurant_service.exception.ResourceNotFoundException;
import com.devdelivery.restaurant_service.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CategoryService {
    @Autowired
    private final CategoryRepository categoryRepository;

    public Page<CategoryResponseDTO> findAll(Pageable pageable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        return categoriesPage.map(CategoryResponseDTO::new);
    }

    public CategoryResponseDTO findById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));

        return convertToDTO(category);
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO requestDTO) {
        Category category = convertToEntity(requestDTO);
        Category savedCategory = categoryRepository.save(category);

        return convertToDTO(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO update(Integer id, CategoryRequestDTO requestDTO) {
        if(!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }

        Category category = convertToEntity(requestDTO);
        category.setId(id);
        Category updatedCategory = categoryRepository.save(category);

        return convertToDTO(updatedCategory);
    }

    @Transactional
    public void delete(Integer id) {
        if(!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }

        categoryRepository.deleteById(id);
    }

    public CategoryResponseDTO convertToDTO(Category category){
        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());

        return dto;
    }

    private Category convertToEntity(CategoryRequestDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
