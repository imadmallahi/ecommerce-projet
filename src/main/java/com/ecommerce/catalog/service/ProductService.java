package com.ecommerce.catalog.service;

import com.ecommerce.catalog.domain.Category;
import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.dto.ProductRequest;
import com.ecommerce.catalog.dto.ProductResponse;
import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.repository.CategoryRepository;
import com.ecommerce.catalog.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        log.debug("Récupération de tous les produits");
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        log.debug("Récupération du produit id={}", id);
        return ProductResponse.from(loadProduct(id));
    }

    public ProductResponse create(ProductRequest request) {
        log.info("Création d'un nouveau produit : nom='{}', prix={}, stock={}",
                request.name(), request.price(), request.stock());
        Product product = new Product(request.name(), request.price(), request.stock());
        Product saved = productRepository.save(product);
        log.info("Produit créé avec succès id={}", saved.getId());
        return ProductResponse.from(saved);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        log.info("Mise à jour du produit id={}", id);
        Product product = loadProduct(id);
        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return ProductResponse.from(product);
    }

    public void delete(Long id) {
        log.info("Suppression du produit id={}", id);
        Product product = loadProduct(id);
        if (product.getCategory() != null) {
            product.getCategory().getProducts().remove(product);
        }
        productRepository.delete(product);
        log.info("Produit id={} supprimé", id);
    }

    public ProductResponse linkToCategory(Long productId, Long categoryId) {
        log.info("Liaison du produit id={} à la catégorie id={}", productId, categoryId);
        Product product = loadProduct(productId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Catégorie introuvable : id={}", categoryId);
                    return new ResourceNotFoundException("Catégorie introuvable : " + categoryId);
                });
        if (product.getCategory() != null) {
            product.getCategory().getProducts().remove(product);
        }
        category.addProduct(product);
        log.info("Produit id={} lié à la catégorie id={}", productId, categoryId);
        return ProductResponse.from(product);
    }

    public ProductResponse unlinkFromCategory(Long productId) {
        log.info("Déliaison du produit id={} de sa catégorie", productId);
        Product product = loadProduct(productId);
        if (product.getCategory() == null) {
            log.debug("Le produit id={} n'a pas de catégorie, aucune action", productId);
            return ProductResponse.from(product);
        }
        product.getCategory().removeProduct(product);
        return ProductResponse.from(product);
    }

    private Product loadProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produit introuvable : id={}", id);
                    return new ResourceNotFoundException("Produit introuvable : " + id);
                });
    }
}
