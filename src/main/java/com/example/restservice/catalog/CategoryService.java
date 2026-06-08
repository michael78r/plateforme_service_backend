package com.example.restservice.catalog;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.restservice.shared.exception.BusinessException;
import com.example.restservice.shared.exception.ResourceNotFoundException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable : " + id));
    }

    @Transactional
    public Category create(Category category) {
        ensureNameAvailable(category.getName(), null);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Category details) {
        Category category = getById(id);
        ensureNameAvailable(details.getName(), id);
        category.setName(details.getName());
        category.setDescription(details.getDescription());
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Catégorie introuvable : " + id);
        }
        categoryRepository.deleteById(id);
    }

    /** Empêche deux catégories de partager le même nom (la colonne est unique en base). */
    private void ensureNameAvailable(String name, Long currentId) {
        categoryRepository.findByName(name)
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new BusinessException("Une catégorie porte déjà ce nom : " + name);
                });
    }
}
