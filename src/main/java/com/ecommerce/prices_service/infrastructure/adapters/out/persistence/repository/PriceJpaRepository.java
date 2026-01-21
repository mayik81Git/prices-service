package com.ecommerce.prices_service.infrastructure.adapters.out.persistence.repository;

import com.ecommerce.prices_service.infrastructure.adapters.out.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio de persistencia basado en Spring Data JPA para la entidad {@link PriceEntity}.
 * <p>
 * Proporciona métodos de acceso a datos optimizados mediante consultas personalizadas en JPQL.
 * Esta interfaz abstrae la comunicación con el motor de base de datos (PostgreSQL/H2)
 * gestionando el ciclo de vida de las entidades de precios.
 * </p>
 */
public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    /**
     * Realiza una búsqueda optimizada para localizar la tarifa vigente con mayor prioridad.
     * <p>
     * La consulta filtra por identificadores de producto y cadena, validando que la fecha
     * de aplicación se encuentre dentro del rango de vigencia de la tarifa.
     * Los resultados se ordenan de forma descendente por prioridad, recuperando únicamente
     * el primer registro coincidente.
     * </p>
     *
     * @param date        Fecha y hora de aplicación para la validación de vigencia.
     * @param productId   Identificador único del producto.
     * @param brandId     Identificador de la cadena o marca.
     * @return Un {@link Optional} con la {@link PriceEntity} aplicable, o vacío si no existe tarifa para esos criterios.
     */
    @Query("""
        SELECT p FROM PriceEntity p 
        WHERE p.productId = :productId 
        AND p.brandId = :brandId 
        AND :date BETWEEN p.startDate AND p.endDate 
        ORDER BY p.priority DESC 
        LIMIT 1
    """)
    Optional<PriceEntity> findTopPrice(
            @Param("date") LocalDateTime date,
            @Param("productId") Long productId,
            @Param("brandId") Long brandId);
}
