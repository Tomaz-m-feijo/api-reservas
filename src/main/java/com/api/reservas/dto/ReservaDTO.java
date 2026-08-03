package com.api.reservas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ReservaDTO(
        @NotBlank(message = "A sala é obrigatória.") String sala,
        @NotBlank(message = "O responsável é obrigatório.") String responsavel,
        @NotNull(message = "O horário de início é obrigatório.") OffsetDateTime inicio,
        @NotNull(message = "O horário de fim é obrigatório.") OffsetDateTime fim
) {}