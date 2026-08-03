package com.api.reservas.service;

import com.api.reservas.dto.ReservaDTO;
import com.api.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        //Simula o repositório vazio
        when(repository.buscarTodas()).thenReturn(List.of());

        List<ReservaDTO> resultado = service.listarReservas();

        //Verifica se a lista retornada está vazia
        assertTrue(resultado.isEmpty());
        verify(repository, times(1)).buscarTodas();
    }

    @Test
    void deveLimparTodasAsReservas() {
        service.limparReservas();

        //Verifica se o serviço repassou a ordem de limpeza para o repositório exatamente uma vez
        verify(repository, times(1)).limparTodas();
    }

}