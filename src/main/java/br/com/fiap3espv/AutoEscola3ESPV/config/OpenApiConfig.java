package br.com.fiap3espv.AutoEscola3ESPV.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("AutoEscola3ESPV API")
                        .description("API REST para gerenciamento de alunos, instrutores, usuários e instruções")
                        .version("v1")
                        .contact(new Contact()
                                .name("Heloísa Fleury Jardim - RM556378\n" +
                                        "Juan Fuentes Rufino - RM557673\n" +
                                        "Rickelmyn de Souza Ruescas - RM556055\n" +
                                        "Paulo Henrique Monteiro Golovanevsky - RM555300\n" +
                                        "Pedro Henrique Silva Batista - RM558137")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}