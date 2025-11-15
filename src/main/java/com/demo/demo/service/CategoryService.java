package com.demo.demo.service;

import com.demo.demo.entity.Category;
import com.demo.demo.exception.exceptions.BadRequestException;
import com.demo.demo.model.request.CategoryRequest;
import com.demo.demo.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // CREATE
    @Transactional
    public CategoryRequest createCategory(CategoryRequest categoryModel) {
        Category category = toEntity(categoryModel);
        Category savedCategory = categoryRepository.save(category);
        return toModel(savedCategory);
    }

    // READ (All)
    public List<CategoryRequest> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    // READ (Single by ID)
    public CategoryRequest getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + id));
        return toModel(category);
    }

    // UPDATE
    @Transactional
    public CategoryRequest updateCategory(Long id, CategoryRequest categoryDetails) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Category not found with id: " + id));

        existingCategory.setName(categoryDetails.getName());
        existingCategory.setDescription(categoryDetails.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return toModel(updatedCategory);
    }

    // DELETE (Physical)
    @Transactional
    public void deleteCategory(Long id) {
        // First, check if the resource exists to provide a clear 404 error if not found.
        if (!categoryRepository.existsById(id)) {
            throw new BadRequestException("Category not found with id: " + id);
        }
        // This performs a physical deletion from the database.
        categoryRepository.deleteById(id);
    }

    // --- Helper methods for mapping between Entity and Model ---

    private CategoryRequest toModel(Category category) {
        CategoryRequest model = new CategoryRequest();
        model.setId(category.getId());
        model.setName(category.getName());
        model.setDescription(category.getDescription());
        return model;
    }

    private Category toEntity(CategoryRequest model) {
        Category category = new Category();
        // We don't map the ID from Model to entity for creation
        category.setName(model.getName());
        category.setDescription(model.getDescription());
        return category;
    }
}
