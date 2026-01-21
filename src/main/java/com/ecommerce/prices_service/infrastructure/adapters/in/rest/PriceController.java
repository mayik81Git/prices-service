package com.ecommerce.prices_service.infrastructure.adapters.in.rest;

import com.ecommerce.prices_service.application.ports.in.GetPriceUseCase;
import com.ecommerce.prices_service.infrastructure.adapters.in.rest.dto.PriceResponseDTO;
import com.ecommerce.prices_service.infrastructure.adapters.in.rest.mapper.PriceRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controlador REST que actúa como adaptador de entrada para la gestión de precios.
 * <p>
 * Expone los endpoints necesarios para la consulta de tarifas vigentes, aplicando
 * validaciones sintácticas y gestionando la transformación de datos entre la
 * capa de transporte y la capa de aplicación.
 * </p>
 */
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/prices")
@Validated
@Tag(name = "Precios", description = "Consulta de tarifas vigentes mediante")
public class PriceController {

    private final GetPriceUseCase getPriceUseCase;
    private final PriceRestMapper priceMapper;

    /**
     * Constructor para la inyección de dependencias del caso de uso y el mapper REST.
     *
     * @param getPriceUseCase Interfaz del caso de uso para obtener precios.
     * @param priceMapper     Mapper para transformar modelos de dominio a DTOs de respuesta.
     */
    public PriceController(GetPriceUseCase getPriceUseCase, PriceRestMapper priceMapper) {
        this.getPriceUseCase = getPriceUseCase;
        this.priceMapper = priceMapper;
    }

    /**
     * Endpoint para consultar la tarifa aplicable con mayor prioridad.
     * <p>
     * El método valida que los parámetros de entrada cumplan con los requisitos de
     * formato y rango antes de delegar la ejecución al caso de uso correspondiente.
     * </p>
     *
     * @param applicationDate Fecha en la que se desea consultar la vigencia del precio (ISO 8601).
     * @param productId       Identificador numérico positivo del producto.
     * @param brandId         Identificador numérico positivo de la cadena/marca.
     * @return {@link ResponseEntity} que contiene el {@link PriceResponseDTO} con la información de la tarifa.
     */
    @Operation(
            summary = "Consultar precio aplicable",
            description = "Devuelve la tarifa con mayor prioridad para un producto, cadena y fecha específica. "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos o reglas de negocio violadas."),
            @ApiResponse(responseCode = "404", description = "No se encontró ningún precio para los criterios proporcionados."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor controlado.")
    })
    @GetMapping
    public ResponseEntity<PriceResponseDTO> getPrice(
            @Parameter(description = "Fecha de aplicación (Formato ISO 8601)", example = "2026-06-14T16:00:00", required = true)
            @RequestParam
            @NotNull(message = "La fecha de aplicación es obligatoria")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime applicationDate,

            @Parameter(description = "ID del producto", example = "35455")
            @RequestParam
            @NotNull(message = "El ID de producto es obligatorio")
            @Positive(message = "El ID de producto debe ser un número positivo")
            @Max(value = Long.MAX_VALUE, message = "El valor excede el límite permitido")
            Long productId,

            @Parameter(description = "ID de la cadena", example = "1")
            @RequestParam
            @NotNull(message = "El ID de cadena es obligatorio")
            @Max(value = Long.MAX_VALUE, message = "El valor excede el límite permitido")
            @Positive(message = "El ID de cadena debe ser un número positivo")
            Long brandId) {

        var price = getPriceUseCase.execute(applicationDate, productId, brandId);

        return ResponseEntity.ok(priceMapper.toResponse(price));
    }
}