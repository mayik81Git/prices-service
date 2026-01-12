package com.ecommerce.prices_service.application.ports.out;

import com.ecommerce.prices_service.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepositoryPort {

    /**
     * Recupera todas las tarifas que coinciden con el producto y la marca,
     * y cuyo rango de validez incluye la fecha proporcionada.
     *
     * @param date      Fecha de aplicación para filtrar el rango [START_DATE, END_DATE].
     * @param productId Identificador del producto.
     * @param brandId   Identificador de la marca.
     * @return Una lista de {@link Price} candidatos. Si no hay coincidencias, devuelve una lista vacía.
     */
    List<Price> findApplicablePrices(LocalDateTime date, Long productId, Long brandId);
}
