package com.ecommerce.catalog.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AjoutProduitRequest(
        @NotNull Long produitId,
        @NotNull @Positive Integer quantite
) {
}
