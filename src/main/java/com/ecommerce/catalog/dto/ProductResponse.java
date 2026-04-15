package com.ecommerce.catalog.dto;

import com.ecommerce.catalog.domain.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock,
        Long categoryId
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory() != null ? product.getCategory().getId() : null
        );
    }
}
