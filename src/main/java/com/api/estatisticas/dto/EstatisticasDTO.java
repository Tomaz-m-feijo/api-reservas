package com.api.estatisticas.dto;

public record EstatisticasDTO(
        long countReservas,
        long salasUtilizadas,
        long tempoTotalReservadoMinutos,
        double mediaDuracaoMinutos,
        long maiorDuracaoMinutos
) {}