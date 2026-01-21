package com.ecommerce.prices_service.infrastructure.config;

import com.ecommerce.prices_service.application.ports.in.GetPriceUseCase;
import com.ecommerce.prices_service.application.ports.out.PriceRepositoryPort;
import com.ecommerce.prices_service.application.services.PriceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public GetPriceUseCase getPriceUseCase(PriceRepositoryPort priceRepositoryPort) {
        return new PriceService(priceRepositoryPort);
    }

}