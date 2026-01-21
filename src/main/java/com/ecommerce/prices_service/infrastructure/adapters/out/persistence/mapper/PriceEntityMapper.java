package com.ecommerce.prices_service.infrastructure.adapters.out.persistence.mapper;

import com.ecommerce.prices_service.domain.model.Price;
import com.ecommerce.prices_service.infrastructure.adapters.out.persistence.entity.PriceEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceEntityMapper {

    Price toDomain(PriceEntity entity);

    // Mapeo manual para evitar el conflicto de introspección con Java 25 Records
    default PriceEntity toEntity(Price domain) {
        if (domain == null) return null;

        PriceEntity entity = new PriceEntity();
        // Seteamos explícitamente usando los métodos del Record
        entity.setBrandId(domain.brandId());
        entity.setProductId(domain.productId());
        entity.setPriceList(domain.priceList());
        entity.setStartDate(domain.startDate());
        entity.setEndDate(domain.endDate());
        entity.setPrice(domain.price());
        entity.setCurrency(String.valueOf(domain.currency()));
        entity.setPriority(domain.priority());

        return entity;
    }
}
