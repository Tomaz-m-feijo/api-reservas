package com.api.estatisticas.controller;
import com.api.estatisticas.dto.EstatisticasDTO;
import com.api.estatisticas.service.EstatisticasService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EstatisticasControllerTests {

    private MockMvc mockMvc;

    @Mock
    private EstatisticasService service;

    @InjectMocks
    private EstatisticasController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveRetornarEstatisticasComStatus200() throws Exception {
        EstatisticasDTO estatisticasMock = new EstatisticasDTO(5, 3, 300, 60.0, 120);
        when(service.calcularEstatisticasDoDia()).thenReturn(estatisticasMock);

        mockMvc.perform(get("/estatisticas")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countReservas").value(5))
                .andExpect(jsonPath("$.salasUtilizadas").value(3))
                .andExpect(jsonPath("$.tempoTotalReservadoMinutos").value(300))
                .andExpect(jsonPath("$.mediaDuracaoMinutos").value(60.0))
                .andExpect(jsonPath("$.maiorDuracaoMinutos").value(120));
    }
}