package com.justintime.jit.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@OpenAPIDefinition(
        info = @Info(
                title = "Just In Time API",
                version = "v1",
                description = "This API uses JWT stored in bearer header for authentication."
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class SwaggerConfig {

        private static final String DEFAULT_DEV_JWT = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE3NTM2MTc2ODMsInN1YiI6InZlbmtpNTcyMDAyQGdtYWlsLmNvbSIsImV4cCI6My42MDAwMDAwMDAwMDE3NTRlKzIxLCJyZXN0YXVyYW50Q29kZSI6IlRHU1IiLCJyb2xlIjoiQ09PSyJ9.ZXlKaGJHY2lPaUpJVXpJMU5pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SnBZWFFpT2pFM05UTTJNVGMyT0RNc0luTjFZaUk2SW5abGJtdHBOVGN5TURBeVFHZHRZV2xzTG1OdmJTSXNJbVY0Y0NJNk15NDJNREF3TURBd01EQXdNREUzTlRSbEt6SXhMQ0p5WlhOMFlYVnlZVzUwUTI5a1pTSTZJbFJIVTFJaUxDSnliMnhsSWpvaVEwOVBTeUo5Ok4zVnpkQ0Z1VkNGdE16VXpZM0psZEV0bGVUUktVMDlPZDJWaVZEQnJNMjV6OlNIQS0yNTY";

        /**
         * Register a custom OpenAPI bean for runtime customization (optional).
         */
        @Bean
        public OpenAPI customizeOpenAPI() {
                return new OpenAPI()
                        .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"))
                        .components(new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes("bearerAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Default Dev Token: `" + DEFAULT_DEV_JWT + "`")));
        }

        /**
         * Optional: Register a default OpenApi group with paths.
         */
        @Bean
        public GroupedOpenApi publicApi(OpenApiCustomizer openApiCustomizer) {
                return GroupedOpenApi.builder()
                        .group("jit")
                        .pathsToMatch("/**")
                        .addOpenApiCustomizer(openApiCustomizer)
                        .build();
        }

        /**
         * Optional: Customize OpenAPI spec to enhance Swagger UI token injection.
         */
        @Bean
        public OpenApiCustomizer openApiCustomizer() {
                return openApi -> {
                        var scheme = openApi.getComponents().getSecuritySchemes().get("bearerAuth");
                        if (scheme != null) {
                                scheme.setDescription("Default Dev JWT Token: `" + DEFAULT_DEV_JWT + "`");
                        }
                };
        }
}
