package com.ecommerce.prices_service.application.ports.in;

import com.ecommerce.prices_service.domain.model.Price;

import java.time.LocalDateTime;

/**
 * Puerto de entrada para el caso de uso de consulta de precios.
 */
public interface GetPriceUseCase {

    /**
     * Ejecuta la lógica de obtención del precio aplicable.
     *
     * @param date        Fecha de aplicación de la tarifa.
     * @param productId   Identificador del producto.
     * @param brandId     Identificador de la cadena.
     * @return El objeto de dominio {@link Price} con la tarifa de mayor prioridad.
     * @throws PriceNotFoundException si no se encuentra ninguna tarifa.
     */
    Price execute(LocalDateTime date, Long productId, Long brandId);
}
