package com.api.reservas.service;

import com.api.reservas.dto.ReservaDTO;
import com.api.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    void deveCriarReservaComSucesso() {
        // Reserva válida amanhã às 14:00 até 15:00
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
        // Reserva às 07:00 (antes das 08:00)
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

        // Tenta reservar das 14:30 às 15:30 na mesma sala
        ReservaDTO nova = new ReservaDTO("A101", "Maria", inicio.plusMinutes(30), inicio.plusMinutes(90));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.criarReserva(nova));
        assertTrue(ex.getMessage().contains("sobreposição de horário"));
    }
}