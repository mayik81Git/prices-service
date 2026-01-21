package com.ecommerce.prices_service.application.services;

import com.ecommerce.prices_service.application.ports.in.GetPriceUseCase;
import com.ecommerce.prices_service.application.ports.out.PriceRepositoryPort;
import com.ecommerce.prices_service.domain.exception.PriceNotFoundException;
import com.ecommerce.prices_service.domain.model.Price;

import java.time.LocalDateTime;

/**
 * Servicio de aplicación que implementa la lógica de negocio para la gestión de precios.
 * <p>
 * Esta clase actúa como un POJO puro dentro de la capa de aplicación, siguiendo los principios
 * de la Arquitectura Hexagonal. Está desacoplada de cualquier framework de infraestructura,
 * facilitando su testabilidad y mantenimiento en entornos de alta concurrencia (Virtual Threads).
 * </p>
 */
public class PriceService implements GetPriceUseCase {

    private final PriceRepositoryPort priceRepository;

    /**
     * Constructor para la inyección de dependencias del puerto de salida.
     *
     * @param priceRepository Adaptador de persistencia para la consulta de tarifas.
     */
    public PriceService(PriceRepositoryPort priceRepository) {
        this.priceRepository = priceRepository;
    }

    /**
     * Ejecuta el proceso de búsqueda de la tarifa aplicable.
     * <p>
     * La selección del precio final se delega a la capa de persistencia mediante una consulta
     * optimizada que filtra por rango de fechas y selecciona la tarifa con el nivel de
     * prioridad más alto.
     * </p>
     *
     * @param date        Instante de tiempo para el cual se requiere validar la vigencia del precio.
     * @param productId   Identificador único del producto en catálogo.
     * @param brandId     Identificador de la cadena o grupo empresarial (ej. Zara = 1).
     * @return {@link Price} El objeto de dominio con la tarifa válida encontrada.
     * @throws PriceNotFoundException Si no existe ningún registro que coincida con los criterios
     *                                de búsqueda en el instante proporcionado.
     */
    @Override
    public Price execute(LocalDateTime date, Long productId, Long brandId) {
        return priceRepository.findPriceByPriority(date, productId, brandId)
                .orElseThrow(() -> new PriceNotFoundException("Precio no encontrado"));
    }
}
