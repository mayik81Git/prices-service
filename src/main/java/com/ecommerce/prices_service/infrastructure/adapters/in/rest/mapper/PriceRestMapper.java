package com.ecommerce.prices_service.infrastructure.adapters.in.rest.mapper;

import com.ecommerce.prices_service.domain.model.Price;
import com.ecommerce.prices_service.infrastructure.adapters.in.rest.dto.PriceResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PriceRestMapper {

    public PriceResponseDTO toResponse(Price domain) {
        if (Objects.isNull(domain)) {
            return null;
        }

        return new PriceResponseDTO(
                domain.getProductId(),
                domain.getBrandId(),
                domain.getPriceList(),
                domain.getStartDate(),
                domain.getEndDate(),
                domain.getAmount()
        );
    }
}