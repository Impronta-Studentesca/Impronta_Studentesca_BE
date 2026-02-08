package it.impronta_studentesca_be.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI improntaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Impronta Studentesca API")
                        .description("API backend per gestione persone, direttivi, rappresentanze, ecc.")
                        .version("v1.0.0")
                        .license(new License().name("MIT").url("https://example.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentazione extra")
                        .url("https://example.com/docs"));
    }
}
