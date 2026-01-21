package com.ecommerce.prices_service.infrastructure.adapters.in.rest.mapper;

import com.ecommerce.prices_service.domain.model.Price;
import com.ecommerce.prices_service.infrastructure.adapters.in.rest.dto.PriceResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Mapper para la transformación entre el modelo de dominio Price (Record)
 * y el DTO de respuesta de la API REST.
 */
@Component
public class PriceRestMapper {

    /**
     * Convierte un objeto de dominio Price a un PriceResponseDTO.
     */
    public PriceResponseDTO toResponse(Price domain) {
        if (Objects.isNull(domain)) {
            return null;
        }

        return new PriceResponseDTO(
                domain.productId(),     // Acceso directo por método del Record
                domain.brandId(),
                domain.priceList(),
                domain.startDate(),
                domain.endDate(),
                domain.price(),         // Ajustado al nombre del campo en el dominio
                domain.currency().getCurrencyCode() // Convertimos Currency a String ISO
        );
    }
}