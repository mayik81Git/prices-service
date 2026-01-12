package com.ecommerce.prices_service.application.ports.in;

import com.ecommerce.prices_service.domain.model.Price;

import java.time.LocalDateTime;

public interface GetPriceUseCase {

    /**
     * Ejecuta el proceso de obtención del precio final aplicable.
     * <p>
     * El proceso incluye la búsqueda en persistencia y la aplicación de reglas
     * de negocio para la resolución de prioridades en rangos de fechas solapados.
     * </p>
     *
     * @param applicationDate Fecha y hora en la que se desea consultar la tarifa vigente.
     * @param productId       Identificador único del producto objeto de la consulta.
     * @param brandId         Identificador de la cadena o marca del grupo.
     * @return {@link Price} El objeto de dominio con los datos de la tarifa encontrada.
     * @throws com.ecommerce.prices_service.domain.exception.PriceNotFoundException si no hay tarifas vigentes.
     */
    Price execute(LocalDateTime applicationDate, Long productId, Long brandId);
}
