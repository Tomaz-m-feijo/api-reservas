package com.api.reservas.service;

import com.api.reservas.dto.ReservaDTO;
import com.api.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher; // Import necessário

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    private ReservaRepository repository;
    private ReservaService service;

    @BeforeEach
    void setUp() {
        repository = mock(ReservaRepository.class);

        service = new ReservaService(repository);
    }

    @Test
    void deveCriarReservaComSucessoEDispararEvento() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        ReservaDTO reserva = new ReservaDTO("A101", "Maria", inicio, inicio.plusHours(1));

        when(repository.buscarTodas()).thenReturn(List.of());

        assertDoesNotThrow(() -> service.criarReserva(reserva));

        verify(repository, times(1)).salvar(reserva);

    }
    @Test
    void naoDevePermitirSalaInvalida() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        ReservaDTO reserva = new ReservaDTO("Z999", "Maria", inicio, inicio.plusHours(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.criarReserva(reserva));
        assertTrue(ex.getMessage().contains("Sala inválida"));
    }

    @Test
    void naoDevePermitirReservaForaDoHorarioComercial() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(7).withMinute(0).withSecond(0).withNano(0);
        ReservaDTO reserva = new ReservaDTO("A101", "Maria", inicio, inicio.plusHours(1));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.criarReserva(reserva));
        assertTrue(ex.getMessage().contains("entre 08:00 e 18:00"));
    }

    @Test
    void naoDevePermitirSobreposicao() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        ReservaDTO existente = new ReservaDTO("A101", "João", inicio, inicio.plusHours(1));

        when(repository.buscarTodas()).thenReturn(List.of(existente));

        ReservaDTO nova = new ReservaDTO("A101", "Maria", inicio.plusMinutes(30), inicio.plusMinutes(90));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.criarReserva(nova));
        assertTrue(ex.getMessage().contains("sobreposição de horário"));
    }

    @Test
    void deveListarTodasAsReservas() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        ReservaDTO r1 = new ReservaDTO("A101", "Maria", inicio, inicio.plusHours(1));
        ReservaDTO r2 = new ReservaDTO("B201", "Joao", inicio.plusHours(2), inicio.plusHours(3));

        when(repository.buscarTodas()).thenReturn(List.of(r1, r2));

        List<ReservaDTO> resultado = service.listarReservas();

        assertEquals(2, resultado.size());
        assertEquals("A101", resultado.get(0).sala());
        assertEquals("B201", resultado.get(1).sala());
        verify(repository, times(1)).buscarTodas();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverReservasNaListagem() {
        when(repository.buscarTodas()).thenReturn(List.of());

        List<ReservaDTO> resultado = service.listarReservas();

        assertTrue(resultado.isEmpty());
        verify(repository, times(1)).buscarTodas();
    }

    @Test
    void deveLimparTodasAsReservas() {
        service.limparReservas();

        verify(repository, times(1)).limparTodas();
    }

}