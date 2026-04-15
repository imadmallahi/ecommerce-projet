package com.ecommerce.catalog.service;

import com.ecommerce.catalog.domain.Category;
import com.ecommerce.catalog.dto.CategoryRequest;
import com.ecommerce.catalog.dto.CategoryResponse;
import com.ecommerce.catalog.exception.BusinessException;
import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findRoots() {
        log.debug("Récupération des catégories racines");
        return categoryRepository.findByParentIsNull().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        log.debug("Récupération de la catégorie id={}", id);
        return CategoryResponse.from(loadCategory(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        log.info("Création d'une nouvelle catégorie : nom='{}'", request.name());
        Category category = new Category(request.name(), request.description());
        Category saved = categoryRepository.save(category);
        log.info("Catégorie créée avec succès id={}", saved.getId());
        return CategoryResponse.from(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        log.info("Mise à jour de la catégorie id={}", id);
        Category category = loadCategory(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return CategoryResponse.from(category);
    }

    public void delete(Long id) {
        log.info("Suppression de la catégorie id={}", id);
        Category category = loadCategory(id);
        if (category.getParent() != null) {
            category.getParent().getChildren().remove(category);
        }
        categoryRepository.delete(category);
        log.info("Catégorie id={} supprimée", id);
    }

    public CategoryResponse linkToParent(Long childId, Long parentId) {
        log.info("Liaison de la catégorie enfant id={} au parent id={}", childId, parentId);
        if (childId.equals(parentId)) {
            log.warn("Tentative de liaison d'une catégorie à elle-même : id={}", childId);
            throw new BusinessException("Une catégorie ne peut pas être son propre parent");
        }
        Category child = loadCategory(childId);
        Category parent = loadCategory(parentId);
        if (createsCycle(parent, child)) {
            log.warn("Liaison refusée : cycle détecté entre enfant id={} et parent id={}", childId, parentId);
            throw new BusinessException("La liaison créerait un cycle dans l'arbre des catégories");
        }
        if (child.getParent() != null) {
            child.getParent().getChildren().remove(child);
        }
        parent.addChild(child);
        log.info("Catégorie id={} liée au parent id={}", childId, parentId);
        return CategoryResponse.from(child);
    }

    public CategoryResponse unlinkFromParent(Long childId) {
        log.info("Déliaison de la catégorie id={} de son parent", childId);
        Category child = loadCategory(childId);
        if (child.getParent() == null) {
            log.debug("La catégorie id={} n'a pas de parent, aucune action", childId);
            return CategoryResponse.from(child);
        }
        child.getParent().removeChild(child);
        return CategoryResponse.from(child);
    }

    private Category loadCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Catégorie introuvable : id={}", id);
                    return new ResourceNotFoundException("Catégorie introuvable : " + id);
                });
    }

    private boolean createsCycle(Category parent, Category child) {
        Category current = parent;
        while (current != null) {
            if (current.getId().equals(child.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
