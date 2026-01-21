package com.ecommerce.prices_service.infrastructure.aspect;

import com.ecommerce.prices_service.application.ports.in.GetPriceUseCase;
import com.ecommerce.prices_service.domain.model.Price;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Decorador de infraestructura que implementa la lógica de resiliencia para el caso de uso
 * de consulta de precios.
 * <p>
 * Esta clase aplica el patrón de diseño <b>Decorator</b> para añadir capacidades de
 * tolerancia a fallos y gestión de concurrencia de forma transversal (AOP). Permite que
 * la lógica de negocio permanezca agnóstica a las librerías de infraestructura y
 * resiliencia.
 * </p>
 *
 * @see GetPriceUseCase
 */
@Component
@Primary
public class PriceUseCaseDecorator implements GetPriceUseCase {

    private final GetPriceUseCase priceService;

    /**
     * Constructor para la inyección de la implementación base del caso de uso.
     *
     * @param priceService Implementación del servicio de aplicación que contiene la lógica pura.
     */
    public PriceUseCaseDecorator(GetPriceUseCase priceService) {
        this.priceService = priceService;
    }

    /**
     * Ejecuta el caso de uso de consulta de precios aplicando políticas de resiliencia.
     * <p>
     * Se aplican las siguientes protecciones:
     * <ul>
     *   <li><b>Bulkhead (SEMAPHORE):</b> Limita el número de llamadas concurrentes para
     *   prevenir la saturación de los recursos del sistema y del pool de conexiones.</li>
     *   <li><b>Circuit Breaker:</b> Supervisa la tasa de fallos y abre el circuito para
     *   evitar llamadas a sistemas inestables o degradados.</li>
     * </ul>
     * </p>
     *
     * @param date        Fecha de aplicación para la búsqueda de la tarifa.
     * @param productId   Identificador único del producto.
     * @param brandId     Identificador de la cadena o marca.
     * @return {@link Price} La tarifa vigente con mayor prioridad.
     * @throws PriceNotFoundException si no se encuentra un precio aplicable.
     */
    @Override
    @Bulkhead(name = "priceService", type = Bulkhead.Type.SEMAPHORE)
    @CircuitBreaker(name = "priceService")
    public Price execute(LocalDateTime date, Long productId, Long brandId) {
        return priceService.execute(date, productId, brandId);
    }
}