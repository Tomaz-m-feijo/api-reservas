package com.api.reservas.controller;

import com.api.reservas.dto.ReservaDTO;
import com.api.core.exception.GlobalExceptionHandler;
import com.api.reservas.service.ReservaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Utiliza o executor nativo do Mockito, sem depender do Spring Boot Test
@ExtendWith(MockitoExtension.class)
class ReservasControllerTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ReservaService service;

    @InjectMocks
    private ReservaController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveRetornar201AoCriarReservaValida() throws Exception {
        ReservaDTO reserva = new ReservaDTO("A101", "Maria", OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(1).plusHours(1));

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar422AoViolarRegraDeNegocio() throws Exception {
        ReservaDTO reserva = new ReservaDTO("A101", "Maria", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1));

        doThrow(new IllegalArgumentException("Erro de negócio")).when(service).criarReserva(any());

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserva)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverReservas() throws Exception {
        when(service.listarReservas()).thenReturn(List.of());

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deveRetornar200AoLimparReservas() throws Exception {
        mockMvc.perform(delete("/reservas"))
                .andExpect(status().isOk());
    }
}