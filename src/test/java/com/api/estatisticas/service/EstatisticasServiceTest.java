package com.api.estatisticas.service;

import com.api.estatisticas.dto.EstatisticasDTO;
import com.api.reservas.dto.ReservaDTO;
import com.api.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EstatisticasServiceTest {

    private ReservaRepository repository;
    private EstatisticasService service;

    @BeforeEach
    void setUp() {
        repository = mock(ReservaRepository.class);
        service = new EstatisticasService(repository);
    }

    @Test
    void deveRetornarEstatisticasZeradasQuandoNaoHouverReservasHoje() {
        when(repository.buscarTodas()).thenReturn(List.of());

        // Act: Calculamos
        EstatisticasDTO stats = service.calcularEstatisticasDoDia();

        // Assert: Validamos se tudo zerou
        assertEquals(0, stats.countReservas());
        assertEquals(0, stats.salasUtilizadas());
        assertEquals(0, stats.tempoTotalReservadoMinutos());
        assertEquals(0.0, stats.mediaDuracaoMinutos());
        assertEquals(0, stats.maiorDuracaoMinutos());
    }

    @Test
    void deveCalcularEstatisticasCorretamenteParaHoje() {
        OffsetDateTime hoje = OffsetDateTime.now();

        // Arrange: Criamos 3 reservas simuladas no banco para o dia de hoje
        ReservaDTO r1 = new ReservaDTO("A101", "João", hoje, hoje.plusMinutes(60));
        ReservaDTO r2 = new ReservaDTO("B201", "Maria", hoje.plusHours(2), hoje.plusHours(2).plusMinutes(120));
        ReservaDTO r3 = new ReservaDTO("A101", "Pedro", hoje.plusHours(5), hoje.plusHours(5).plusMinutes(30));

        when(repository.buscarTodas()).thenReturn(List.of(r1, r2, r3));

        // Act: Executa o método
        EstatisticasDTO stats = service.calcularEstatisticasDoDia();

        // Assert: Valida a matemática
        assertEquals(3, stats.countReservas());
        assertEquals(2, stats.salasUtilizadas()); // A101 repetiu, são 2 salas distintas
        assertEquals(210, stats.tempoTotalReservadoMinutos()); // 60 + 120 + 30
        assertEquals(70.0, stats.mediaDuracaoMinutos()); // 210 / 3
        assertEquals(120, stats.maiorDuracaoMinutos()); // A maior tem 120
    }

    @Test
    void deveIgnorarReservasDeOutrosDiasNoCalculo() {
        OffsetDateTime ontem = OffsetDateTime.now().minusDays(1);
        OffsetDateTime hoje = OffsetDateTime.now();

        // Arrange: Uma reserva de ontem e uma de hoje
        ReservaDTO reservaOntem = new ReservaDTO("A101", "João", ontem, ontem.plusMinutes(60));
        ReservaDTO reservaHoje = new ReservaDTO("B201", "Maria", hoje, hoje.plusMinutes(90));

        when(repository.buscarTodas()).thenReturn(List.of(reservaOntem, reservaHoje));

        // Act
        EstatisticasDTO stats = service.calcularEstatisticasDoDia();

        // Assert: Só deve contabilizar a reserva de hoje (90 minutos)
        assertEquals(1, stats.countReservas());
        assertEquals(1, stats.salasUtilizadas());
        assertEquals(90, stats.tempoTotalReservadoMinutos());
    }
}