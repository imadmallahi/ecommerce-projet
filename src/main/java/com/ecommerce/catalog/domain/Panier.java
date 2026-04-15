package com.ecommerce.catalog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paniers")
@Getter
@Setter
@NoArgsConstructor
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL)
    private List<LignePanier> lignes = new ArrayList<>();

    public void ajouterProduit(Product produit, int quantite) {
        LignePanier ligne = new LignePanier(this, produit, quantite);
        lignes.add(ligne);
    }

    public BigDecimal getMontantTotal() {
        return lignes.stream()
                .map(LignePanier::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
