package com.ecommerce.prices_service.infrastructure.adapters.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO inmutable para la respuesta de consulta de precios.
 */
@Schema(description = "Modelo de respuesta con el precio final aplicable y su vigencia")
public record PriceResponseDTO(
        @Schema(description = "Identificador único del producto", example = "35455")
        Long productId,

        @Schema(description = "Identificador de la cadena (Brand)", example = "1")
        Long brandId,

        @Schema(description = "Identificador de la lista de precios aplicable", example = "1")
        Integer priceList,

        @Schema(description = "Fecha de inicio de la tarifa (ISO 8601)", example = "2026-06-14T00:00:00")
        LocalDateTime startDate,

        @Schema(description = "Fecha de fin de la tarifa (ISO 8601)", example = "2026-12-31T23:59:59")
        LocalDateTime endDate,

        @Schema(description = "Importe final del precio", example = "35.50")
        BigDecimal price,

        @Schema(description = "Moneda en formato ISO 4217", example = "EUR")
        String currency
) {}
