package com.ecommerce.prices_service.application.ports.out;

import com.ecommerce.prices_service.domain.model.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepositoryPort {

    /**
     * Busca el precio aplicable con mayor prioridad.
     */
    Optional<Price> findPriceByPriority(LocalDateTime date, Long productId, Long brandId);
}
