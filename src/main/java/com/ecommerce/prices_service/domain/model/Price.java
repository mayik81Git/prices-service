package com.ecommerce.prices_service.domain.model;

import com.ecommerce.prices_service.domain.exception.DomainValidationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Objects;

public record Price(
        Long id,
        Long brandId,
        Long productId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        Currency currency,
        Integer priority
) {
    public Price {
        // Validación
        Objects.requireNonNull(brandId, "El brandId es obligatorio");
        Objects.requireNonNull(productId, "El productId es obligatorio");
        Objects.requireNonNull(startDate, "La fecha de inicio es obligatoria");
        Objects.requireNonNull(endDate, "La fecha de fin es obligatoria");
        Objects.requireNonNull(price, "El precio es obligatorio");
        Objects.requireNonNull(currency, "La moneda es obligatoria");

        // Regla 1: Validar integridad de fechas
        if (startDate.isAfter(endDate)) {
            throw new DomainValidationException("La fecha de inicio no puede ser posterior a la de fin");
        }

        // Regla 2: El precio debe ser siempre positivo
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainValidationException("El precio debe ser un valor positivo");
        }

        // Regla 3: Prioridad no negativa (Uso de requireNonNullElse para valor por defecto)
        priority = Objects.requireNonNullElse(priority, 0);
        if (priority < 0) {
            throw new DomainValidationException("La prioridad no puede ser negativa");
        }

    }

    public boolean isApplicableAt(LocalDateTime applicationDate) {
        Objects.requireNonNull(applicationDate, "La fecha de aplicación no puede ser nula");
        return !applicationDate.isBefore(startDate) && !applicationDate.isAfter(endDate);
    }
}