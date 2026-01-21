package com.ecommerce.prices_service.infrastructure.adapters.in.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Suite de pruebas de integración para PriceController.
 * Valida los requisitos funcionales de negocio y la robustez del sistema de errores (RFC 9457).
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser
@DisplayName("Integración - Price API (Java 25)")
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Requisito funcional: Los 5 casos de prueba de la especificación técnica.
     * Verifica que se aplique la tarifa correcta según fecha y prioridad.
     */
    @ParameterizedTest(name = "Caso {0}: Fecha {1} -> Esperado {4}€ (Tarifa {5})")
    @CsvSource({
            "1, 2020-06-14T10:00:00, 35455, 1, 35.50, 1",
            "2, 2020-06-14T16:00:00, 35455, 1, 25.45, 2",
            "3, 2020-06-14T21:00:00, 35455, 1, 35.50, 1",
            "4, 2020-06-15T10:00:00, 35455, 1, 30.50, 3",
            "5, 2020-06-16T21:00:00, 35455, 1, 38.95, 4"
    })
    @DisplayName("Escenarios de Negocio: Validación de prioridad y fechas")
    void getPrice_ShouldReturnCorrectTariff_ForTechnicalScenarios(
            int caseNum, String date, Long productId, Long brandId,
            double expectedPrice, int expectedPriceList) throws Exception {

        // Log informativo para utilizar el parámetro caseNum y que no marque warning
        System.out.println("Ejecutando escenario de prueba técnica número: " + caseNum);

        mockMvc.perform(get("/api/v1/prices")
                        .with(jwt()) // Simula token JWT para evitar 401
                        .param("applicationDate", date)
                        .param("productId", productId.toString())
                        .param("brandId", brandId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(expectedPrice))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList))
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    @DisplayName("Error 404: Producto no encontrado con contexto de diagnóstico")
    void getPrice_ShouldReturn404_WhenPriceNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .with(jwt())
                        .param("applicationDate", "2026-01-20T21:00:00")
                        .param("productId", "99999")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Tarifa No Localizada"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.queryContext.productId").value("99999"))
                .andExpect(jsonPath("$.instance").value("/api/v1/prices"));
    }

    @Test
    @DisplayName("Error 400: Formato de fecha incorrecto con sugerencia ISO")
    void getPrice_ShouldReturn400_WhenInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .with(jwt())
                        .param("applicationDate", "14-06-2020") // Formato no ISO
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Tipo de Parámetro Incorrecto"))
                .andExpect(jsonPath("$.detail").value(containsString("El formato de fecha debe ser ISO")));
    }

    @Test
    @DisplayName("Error 400: Tipo de dato inválido en productId (Captura de contexto)")
    void getPrice_ShouldReturn400_WhenInvalidDataType() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .with(jwt())
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "abc") // Texto en lugar de Long
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.parameter").value("productId"))
                .andExpect(jsonPath("$.detail").value(containsString("espera un valor de tipo Long")));
    }

    @Test
    @DisplayName("Error 400: Parámetro brandId ausente")
    void getPrice_ShouldReturn400_WhenMissingParameter() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .with(jwt())
                        .param("applicationDate", "2020-06-14T10:00:00")
                        .param("productId", "35455")) // Falta brandId
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }
}