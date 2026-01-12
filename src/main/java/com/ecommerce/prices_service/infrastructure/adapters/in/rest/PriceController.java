package com.ecommerce.prices_service.infrastructure.adapters.in.rest;

import com.ecommerce.prices_service.application.ports.in.GetPriceUseCase;
import com.ecommerce.prices_service.domain.model.Price;
import com.ecommerce.prices_service.infrastructure.adapters.in.rest.dto.PriceResponseDTO;
import com.ecommerce.prices_service.infrastructure.adapters.in.rest.mapper.PriceRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/prices")
public class PriceController {

    private final GetPriceUseCase getPriceUseCase;
    private final PriceRestMapper priceMapper;

    public PriceController(GetPriceUseCase getPriceUseCase, PriceRestMapper priceMapper) {
        this.getPriceUseCase = getPriceUseCase;
        this.priceMapper = priceMapper;
    }

    @Operation(summary = "Consultar precio aplicable",
            description = "Devuelve la tarifa con mayor prioridad para un producto, cadena y fecha específica.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Operación exitosa. Se devuelve el precio aplicable."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de entrada inválidos (formato de fecha incorrecto o tipos de datos erróneos)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró ningún precio para los criterios proporcionados."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor."
            )
    })
    @GetMapping
    public ResponseEntity<PriceResponseDTO> getPrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam Long productId,
            @RequestParam Long brandId) {

        Price price = getPriceUseCase.execute(applicationDate, productId, brandId);
        return ResponseEntity.ok(priceMapper.toResponse(price));
    }
}
