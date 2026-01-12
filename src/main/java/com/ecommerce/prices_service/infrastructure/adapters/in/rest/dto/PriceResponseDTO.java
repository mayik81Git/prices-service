package com.ecommerce.prices_service.infrastructure.adapters.in.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceResponseDTO(
        @Schema(example = "35455") Long productId,
        @Schema(example = "1") Long brandId,
        @Schema(example = "1") Integer priceList,
        @Schema(example = "2020-06-14T00:00:00") LocalDateTime startDate,
        @Schema(example = "2020-12-31T23:59:59") LocalDateTime endDate,
        @Schema(example = "35.50") BigDecimal price
) {}
