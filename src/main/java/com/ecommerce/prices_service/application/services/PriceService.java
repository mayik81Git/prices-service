package com.ecommerce.prices_service.application.services;

import com.ecommerce.prices_service.application.ports.in.GetPriceUseCase;
import com.ecommerce.prices_service.application.ports.out.PriceRepositoryPort;
import com.ecommerce.prices_service.domain.exception.PriceNotFoundException;
import com.ecommerce.prices_service.domain.model.Price;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.time.LocalDateTime;
import java.util.Comparator;

public class PriceService implements GetPriceUseCase {

    private final PriceRepositoryPort priceRepositoryPort;

    public PriceService(PriceRepositoryPort priceRepositoryPort) {
        this.priceRepositoryPort = priceRepositoryPort;
    }

    /**
     * <h2>PriceService</h2>
     * Servicio de aplicación encargado de orquestar la lógica de consulta de precios.
     * <p>
     * Este servicio implementa el caso de uso {@link GetPriceUseCase} y coordina la
     * obtención de datos a través del puerto de salida {@link PriceRepositoryPort}.
     * </p>
     *
     * <b>Estrategias de Robustez:</b>
     * <ul>
     *     <li><b>Circuit Breaker:</b> Implementado mediante Resilience4j para evitar el colapso del sistema
     *     si la persistencia presenta fallos o latencias elevadas.</li>
     *     <li><b>Bulkhead:</b> Limita el número de llamadas concurrentes para proteger la disponibilidad de
     *     hilos de ejecución del microservicio ante una alta demanda.</li>
     * </ul>
     *
     */
    @Override
    @CircuitBreaker(name = "priceService")
    @Bulkhead(name = "priceService")
    public Price execute(LocalDateTime applicationDate, Long productId, Long brandId) {
        return priceRepositoryPort.findApplicablePrices(applicationDate, productId, brandId)
                .stream()
                .max(Comparator.comparingInt(Price::getPriority))
                .orElseThrow(() -> new PriceNotFoundException("Ningún precio encontrado para los parámetros pasados"));
    }
}
