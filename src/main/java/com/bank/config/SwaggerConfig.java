package com.bank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI transactionAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Bank System Transaction API")
                .description("API for managing bank transactions")
                .version("v1.0.0"));
    }
}
