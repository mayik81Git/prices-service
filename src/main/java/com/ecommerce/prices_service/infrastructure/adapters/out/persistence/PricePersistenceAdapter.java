package com.ecommerce.prices_service.infrastructure.adapters.out.persistence;

import com.ecommerce.prices_service.application.ports.out.PriceRepositoryPort;
import com.ecommerce.prices_service.domain.model.Price;
import com.ecommerce.prices_service.infrastructure.adapters.out.persistence.repository.PriceJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PricePersistenceAdapter implements PriceRepositoryPort {

    private final PriceJpaRepository jpaRepository;

    public PricePersistenceAdapter(PriceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Obtiene los precios aplicables desde la base de datos PostgreSQL.
     * <p>
     * La anotación {@link Transactional} con readOnly=true optimiza la
     * conexión en el pool Hikari para operaciones de consulta.
     * </p>
     */
    @Override
    @Transactional(readOnly = true)
    public List<Price> findApplicablePrices(LocalDateTime date, Long productId, Long brandId) {
        return jpaRepository.findApplicablePrices(date, productId, brandId)
                .stream()
                .map(entity -> new Price(
                        entity.getBrandId(),
                        entity.getStartDate(),
                        entity.getEndDate(),
                        entity.getPriceList(),
                        entity.getProductId(),
                        entity.getPriority(),
                        entity.getPrice(),
                        entity.getCurr()
                ))
                .collect(Collectors.toList());
    }
}
