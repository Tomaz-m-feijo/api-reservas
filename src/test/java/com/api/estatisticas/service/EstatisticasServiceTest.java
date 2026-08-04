package com.api.estatisticas.service;

import com.api.estatisticas.dto.EstatisticasDTO;
import com.api.reservas.dto.ReservaDTO;
import com.api.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        // Arrange: Simulamos que buscarPorData retorna vazio
        when(repository.buscarPorData(any(LocalDate.class))).thenReturn(List.of());

        // Act
        EstatisticasDTO stats = service.calcularEstatisticasDoDia();

        // Assert
        assertEquals(0, stats.countReservas());
        assertEquals(0, stats.salasUtilizadas());
        assertEquals(0, stats.tempoTotalReservadoMinutos());
        assertEquals(0.0, stats.mediaDuracaoMinutos());
        assertEquals(0, stats.maiorDuracaoMinutos());
    }

    @Test
    void deveCalcularEstatisticasCorretamenteParaHoje() {
        OffsetDateTime hoje = OffsetDateTime.now();

        // Arrange
        ReservaDTO r1 = new ReservaDTO("A101", "João", hoje, hoje.plusMinutes(60));
        ReservaDTO r2 = new ReservaDTO("B201", "Maria", hoje.plusHours(2), hoje.plusHours(2).plusMinutes(120));
        ReservaDTO r3 = new ReservaDTO("A101", "Pedro", hoje.plusHours(5), hoje.plusHours(5).plusMinutes(30));

        // AGORA MOCKAMOS O MÉTODO NOVO: buscarPorData
        when(repository.buscarPorData(hoje.toLocalDate())).thenReturn(List.of(r1, r2, r3));

        // Act
        EstatisticasDTO stats = service.calcularEstatisticasDoDia();

        // Assert
        assertEquals(3, stats.countReservas());
        assertEquals(2, stats.salasUtilizadas());
        assertEquals(210, stats.tempoTotalReservadoMinutos());
        assertEquals(70.0, stats.mediaDuracaoMinutos());
        assertEquals(120, stats.maiorDuracaoMinutos());
    }

    @Test
    void deveIgnorarReservasDeOutrosDiasNoCalculo() {
        OffsetDateTime hoje = OffsetDateTime.now();

        // Arrange: Como a filtragem agora é feita no Repositório (Otimização O(1)),
        ReservaDTO reservaHoje = new ReservaDTO("B201", "Maria", hoje, hoje.plusMinutes(90));

        when(repository.buscarPorData(hoje.toLocalDate())).thenReturn(List.of(reservaHoje));

        // Act
        EstatisticasDTO stats = service.calcularEstatisticasDoDia();

        // Assert: Só deve contabilizar a reserva de hoje retornado pelo repositório
        assertEquals(1, stats.countReservas());
        assertEquals(1, stats.salasUtilizadas());
        assertEquals(90, stats.tempoTotalReservadoMinutos());
    }
}