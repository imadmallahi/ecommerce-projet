package com.ecommerce.catalog.dto;

import com.ecommerce.catalog.domain.Category;

import java.util.List;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        Long parentId,
        List<CategoryResponse> children,
        List<ProductResponse> products
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getChildren().stream().map(CategoryResponse::from).toList(),
                category.getProducts().stream().map(ProductResponse::from).toList()
        );
    }

    public static CategoryResponse summary(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParent() != null ? category.getParent().getId() : null,
                List.of(),
                List.of()
        );
    }
}
