package com.api.estatisticas.controller;

import com.api.estatisticas.dto.EstatisticasDTO;
import com.api.estatisticas.service.EstatisticasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/estatisticas")
@Tag(name = "Estatísticas", description = "Endpoints para consulta de métricas e estatísticas diárias das reservas")
public class EstatisticasController {

    private final EstatisticasService service;

    public EstatisticasController(EstatisticasService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Consultar estatísticas do dia", description = "Retorna os cálculos de ocupação de salas e durações das reservas para o dia atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas calculadas e retornadas com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstatisticasDTO.class)))
    })
    public EstatisticasDTO consultarEstatisticas() {
        return service.calcularEstatisticasDoDia();
    }
}