package com.ecommerce.prices_service.infrastructure.adapters.in.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT = "/api/v1/prices";
    private static final String PRODUCT_ID = "35455";
    private static final String BRAND_ID = "1";

    /**
     * Test 1: Petición a las 10:00 del día 14 del producto 35455 para la brand 1 (ZARA).
     */
    @Test
    @WithMockUser // Simula usuario autenticado
    @DisplayName("Test 1: 14-Jun 10:00 - Tarifa 1 (35.50 EUR)")
    void test1() throws Exception {
        performRequest("2020-06-14T10:00:00", 35.50, 1);
    }

    /**
     * Test 2: Petición a las 16:00 del día 14 del producto 35455 para la brand 1 (ZARA).
     * Valida la prioridad de la tarifa 2 sobre la tarifa 1.
     */
    @Test
    @WithMockUser // Simula usuario autenticado
    @DisplayName("Test 2: 14-Jun 16:00 - Tarifa 2 (25.45 EUR) [Prioridad]")
    void test2() throws Exception {
        performRequest("2020-06-14T16:00:00", 25.45, 2);
    }

    /**
     * Test 3: Petición a las 21:00 del día 14 del producto 35455 para la brand 1 (ZARA).
     */
    @Test
    @WithMockUser // Simula usuario autenticado
    @DisplayName("Test 3: 14-Jun 21:00 - Tarifa 1 (35.50 EUR)")
    void test3() throws Exception {
        performRequest("2020-06-14T21:00:00", 35.50, 1);
    }

    /**
     * Test 4: Petición a las 10:00 del día 15 del producto 35455 para la brand 1 (ZARA).
     */
    @Test
    @WithMockUser // Simula usuario autenticado
    @DisplayName("Test 4: 15-Jun 10:00 - Tarifa 3 (30.50 EUR)")
    void test4() throws Exception {
        performRequest("2020-06-15T10:00:00", 30.50, 3);
    }

    /**
     * Test 5: Petición a las 21:00 del día 16 del producto 35455 para la brand 1 (ZARA).
     */
    @Test
    @WithMockUser // Simula usuario autenticado
    @DisplayName("Test 5: 16-Jun 21:00 - Tarifa 4 (38.95 EUR)")
    void test5() throws Exception {
        performRequest("2020-06-16T21:00:00", 38.95, 4);
    }

    /**
     * Método auxiliar para centralizar la lógica de petición MockMvc con seguridad JWT.
     */
    private void performRequest(String date, double expectedPrice, int expectedPriceList) throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .with(jwt()) // Inyecta la autenticación JWT necesaria en 2026
                        .param("applicationDate", date)
                        .param("productId", PRODUCT_ID)
                        .param("brandId", BRAND_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(expectedPrice))
                .andExpect(jsonPath("$.priceList").value(expectedPriceList));
    }
}