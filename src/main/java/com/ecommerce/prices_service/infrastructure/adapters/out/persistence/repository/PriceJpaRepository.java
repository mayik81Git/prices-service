package com.ecommerce.prices_service.infrastructure.adapters.out.persistence.repository;

import com.ecommerce.prices_service.infrastructure.adapters.out.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    @Query("SELECT p FROM PriceEntity p WHERE p.productId = :productId " +
            "AND p.brandId = :brandId " +
            "AND :applicationDate BETWEEN p.startDate AND p.endDate")
    List<PriceEntity> findApplicablePrices(
            @Param("applicationDate") LocalDateTime applicationDate,
            @Param("productId") Long productId,
            @Param("brandId") Long brandId);
}
