package com.ecommerce.catalog.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes_panier")
@Getter
@Setter
@NoArgsConstructor
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "panier_id")
    @JsonIgnore
    private Panier panier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product produit;

    @Column(nullable = false)
    private Integer quantite;

    public LignePanier(Panier panier, Product produit, Integer quantite) {
        this.panier = panier;
        this.produit = produit;
        this.quantite = quantite;
    }

    public BigDecimal getSousTotal() {
        return produit.getPrice().multiply(BigDecimal.valueOf(quantite));
    }
}
