package com.devdelivery.restaurant_service.repository;

import com.devdelivery.restaurant_service.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve salvar e buscar categoria pelo ID")
    public void shouldSaveAndFindCategoryById() {
        Category category = new Category();
        category.setName("Sobremesas");

        Category savedCategory = categoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        assertNotNull(savedCategory.getId());

        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());
        assertTrue(foundCategory.isPresent());
        assertEquals("Sobremesas", foundCategory.get().getName());
    }

    @Test
    @DisplayName("Deve buscar todas as categorias paginadas")
    public void shouldFindAllCategoriesPaged() {
        createAndSaveCategory("Lanches");
        createAndSaveCategory("Bebidas");

        entityManager.flush();
        entityManager.clear();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        assertFalse(categoryPage.isEmpty());
        assertTrue(categoryPage.getTotalElements() >= 2);
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando a categoria existir pelo ID")
    public void shouldReturnTrueWhenCategoryExistsById() {
        Category category = createAndSaveCategory("Pizzas");

        entityManager.flush();
        entityManager.clear();

        boolean exists = categoryRepository.existsById(category.getId());

        assertTrue(exists);
    }

    @Test
    @DisplayName("Deve retornar falso quando a categoria não existir")
    public void shouldReturnFalseWhenCategoryDoesNotExist() {
        boolean exists = categoryRepository.existsById(999);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Deve atualizar categoria")
    public void shouldUpdateCategory() {
        Category category = createAndSaveCategory("Nome Antigo");

        category.setName("Nome Atualizado");
        Category updatedCategory = categoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        Optional<Category> foundCategory = categoryRepository.findById(updatedCategory.getId());
        assertTrue(foundCategory.isPresent());
        assertEquals("Nome Atualizado", foundCategory.get().getName());
    }

    @Test
    @DisplayName("Deve excluir categoria com sucesso")
    public void shouldDeleteCategorySuccessfully() {
        Category category = createAndSaveCategory("Porções");

        categoryRepository.deleteById(category.getId());

        entityManager.flush();
        entityManager.clear();

        Optional<Category> deletedCategory = categoryRepository.findById(category.getId());
        assertTrue(deletedCategory.isEmpty());
        assertFalse(categoryRepository.existsById(category.getId()));
    }

    private Category createAndSaveCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }
}