package com.ecommerce.catalog.service;

import com.ecommerce.catalog.domain.Panier;
import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.dto.AjoutProduitRequest;
import com.ecommerce.catalog.dto.PanierResponse;
import com.ecommerce.catalog.exception.ResourceNotFoundException;
import com.ecommerce.catalog.repository.PanierRepository;
import com.ecommerce.catalog.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PanierService {

    private static final Logger log = LoggerFactory.getLogger(PanierService.class);

    private final PanierRepository panierRepository;
    private final ProductRepository productRepository;

    public PanierService(PanierRepository panierRepository, ProductRepository productRepository) {
        this.panierRepository = panierRepository;
        this.productRepository = productRepository;
    }

    public PanierResponse creerPanier() {
        log.info("Création d'un nouveau panier");
        Panier panier = panierRepository.save(new Panier());
        log.info("Panier créé avec succès id={}", panier.getId());
        return PanierResponse.from(panier);
    }

    @Transactional(readOnly = true)
    public PanierResponse consulterPanier(Long panierId) {
        log.debug("Consultation du panier id={}", panierId);
        return PanierResponse.from(chargerPanier(panierId));
    }

    public PanierResponse ajouterProduit(Long panierId, AjoutProduitRequest request) {
        log.info("Ajout du produit id={} (quantité={}) au panier id={}",
                request.produitId(), request.quantite(), panierId);
        Panier panier = chargerPanier(panierId);
        Product produit = productRepository.findById(request.produitId())
                .orElseThrow(() -> {
                    log.warn("Produit introuvable : id={}", request.produitId());
                    return new ResourceNotFoundException("Produit introuvable : " + request.produitId());
                });
        panier.ajouterProduit(produit, request.quantite());
        return PanierResponse.from(panier);
    }

    private Panier chargerPanier(Long id) {
        return panierRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Panier introuvable : id={}", id);
                    return new ResourceNotFoundException("Panier introuvable : " + id);
                });
    }
}
