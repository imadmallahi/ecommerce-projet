package com.ecommerce.catalog.dto;

import com.ecommerce.catalog.domain.LignePanier;

import java.math.BigDecimal;

public record LignePanierResponse(
        Long id,
        Long produitId,
        String nomProduit,
        BigDecimal prixUnitaire,
        Integer quantite,
        BigDecimal sousTotal
) {
    public static LignePanierResponse from(LignePanier ligne) {
        return new LignePanierResponse(
                ligne.getId(),
                ligne.getProduit().getId(),
                ligne.getProduit().getName(),
                ligne.getProduit().getPrice(),
                ligne.getQuantite(),
                ligne.getSousTotal()
        );
    }
}
