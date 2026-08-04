package com.api.health.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Monitoramento", description = "Endpoint para verificação de disponibilidade da aplicação")
public class HealthCheckController {

    @GetMapping
    @Operation(summary = "Verificar status da API", description = "Retorna 200 OK se a aplicação estiver no ar.")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}