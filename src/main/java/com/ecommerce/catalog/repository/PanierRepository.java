package com.ecommerce.catalog.repository;

import com.ecommerce.catalog.domain.Panier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PanierRepository extends JpaRepository<Panier, Long> {
}
