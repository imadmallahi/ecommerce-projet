package com.ecommerce.catalog.dto;

import com.ecommerce.catalog.domain.Panier;

import java.math.BigDecimal;
import java.util.List;

public record PanierResponse(
        Long id,
        List<LignePanierResponse> lignes,
        BigDecimal montantTotal
) {
    public static PanierResponse from(Panier panier) {
        return new PanierResponse(
                panier.getId(),
                panier.getLignes().stream().map(LignePanierResponse::from).toList(),
                panier.getMontantTotal()
        );
    }
}
