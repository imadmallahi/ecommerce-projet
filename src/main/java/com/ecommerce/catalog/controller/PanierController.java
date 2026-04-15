package com.ecommerce.catalog.controller;

import com.ecommerce.catalog.dto.AjoutProduitRequest;
import com.ecommerce.catalog.dto.PanierResponse;
import com.ecommerce.catalog.service.PanierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/paniers")
public class PanierController {

    private final PanierService panierService;

    public PanierController(PanierService panierService) {
        this.panierService = panierService;
    }

    @PostMapping
    public ResponseEntity<PanierResponse> creer() {
        PanierResponse panier = panierService.creerPanier();
        return ResponseEntity.created(URI.create("/api/paniers/" + panier.id())).body(panier);
    }

    @GetMapping("/{id}")
    public PanierResponse consulter(@PathVariable Long id) {
        return panierService.consulterPanier(id);
    }

    @PostMapping("/{id}/produits")
    public PanierResponse ajouterProduit(@PathVariable Long id,
                                         @Valid @RequestBody AjoutProduitRequest request) {
        return panierService.ajouterProduit(id, request);
    }
}
