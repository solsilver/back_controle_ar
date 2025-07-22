package com.projetoTCC.arCondicionado.arCondicionado.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "API Ar Condicionado", version = "1.0"),
    security = @SecurityRequirement(name = "bearerAuth") // <- aplica globalmente
)
@SecurityScheme(
    name = "bearerAuth",              // Nome do esquema
    type = SecuritySchemeType.HTTP,  // Tipo: HTTP
    scheme = "bearer",               // Prefixo do header: Bearer
    bearerFormat = "JWT"             // Apenas informativo: mostra "JWT"
)
public class OpenApiConfig {
}