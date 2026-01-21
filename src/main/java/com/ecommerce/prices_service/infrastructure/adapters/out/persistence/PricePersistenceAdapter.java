package com.ecommerce.prices_service.infrastructure.adapters.out.persistence;

import com.ecommerce.prices_service.application.ports.out.PriceRepositoryPort;
import com.ecommerce.prices_service.domain.model.Price;
import com.ecommerce.prices_service.infrastructure.adapters.out.persistence.mapper.PriceEntityMapper;
import com.ecommerce.prices_service.infrastructure.adapters.out.persistence.repository.PriceJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Adaptador de persistencia que implementa el puerto de salida para la gestión de precios.
 * <p>
 * Esta clase actúa como un puente entre la capa de dominio y la infraestructura de base de datos.
 * Se encarga de delegar la lógica de consulta al repositorio JPA y de transformar las entidades
 * de base de datos en objetos de dominio inmutables mediante el uso de mappers.
 * </p>
 */
@Component
public class PricePersistenceAdapter implements PriceRepositoryPort {

    private final PriceJpaRepository priceJpaRepository;
    private final PriceEntityMapper priceEntityMapper;

    /**
     * Constructor para la inyección de dependencias del repositorio JPA y el mapper de entidades.
     *
     * @param priceJpaRepository Repositorio Spring Data JPA para el acceso a datos.
     * @param priceEntityMapper  Mapper para la conversión entre PriceEntity y el modelo de dominio Price.
     */
    public PricePersistenceAdapter(PriceJpaRepository priceJpaRepository, PriceEntityMapper priceEntityMapper) {
        this.priceJpaRepository = priceJpaRepository;
        this.priceEntityMapper = priceEntityMapper;
    }

    /**
     * Recupera el precio aplicable con mayor prioridad desde la base de datos.
     * <p>
     * La consulta se realiza de forma optimizada en el motor de base de datos, filtrando por
     * producto, cadena y rango de fechas, devolviendo únicamente el registro de mayor prioridad
     * mapeado al dominio.
     * </p>
     *
     * @param date        Fecha de aplicación de la tarifa.
     * @param productId   Identificador único del producto.
     * @param brandId     Identificador de la cadena.
     * @return Un {@link Optional} que contiene el {@link Price} encontrado, o vacío si no hay coincidencias.
     */
    @Override
    public Optional<Price> findPriceByPriority(LocalDateTime date, Long productId, Long brandId) {
        return priceJpaRepository.findTopPrice(date, productId, brandId)
                .map(priceEntityMapper::toDomain);
    }
}
