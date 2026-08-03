package com.api.reservas.controller;

import com.api.reservas.dto.ReservaDTO;
import com.api.reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Endpoints para gerenciamento de reservas de salas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar uma nova reserva", description = "Registra uma reserva validando horários, duração e sobreposição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva aceita e registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição malformada (JSON inválido)"),
            @ApiResponse(responseCode = "422", description = "Reserva inválida por regra de negócio")
    })
    public void criarReserva(@Valid @RequestBody ReservaDTO reserva) {
        service.criarReserva(reserva);
    }
}