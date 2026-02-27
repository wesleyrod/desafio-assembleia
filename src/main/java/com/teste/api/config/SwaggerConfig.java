package com.teste.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Sessões de Votação")
                        .version("v1.0.0")
                        .description("Documentação da API RESTful para gerenciamento de pautas, associados e sessões de assembleia.")
                        .contact(new Contact()
                                .name("Wesley Miranda")
                                .email("wesleyrod.br@gmail.com")
                                .url("https://github.com/wesleyrodbr")));
    }
}
