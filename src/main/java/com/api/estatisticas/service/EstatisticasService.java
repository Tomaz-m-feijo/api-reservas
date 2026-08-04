package com.api.estatisticas.service;

import com.api.estatisticas.dto.EstatisticasDTO;
import com.api.reservas.dto.ReservaDTO;
import com.api.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class EstatisticasService {

    private final ReservaRepository repository;

    public EstatisticasService(ReservaRepository repository) {
        this.repository = repository;
    }

    public EstatisticasDTO calcularEstatisticasDoDia() {
        LocalDate hoje = LocalDate.now();

        List<ReservaDTO> reservasDoDia = repository.buscarPorData(hoje);

        if (reservasDoDia.isEmpty()) {
            return new EstatisticasDTO(0, 0, 0, 0.0, 0);
        }

        long countReservas = reservasDoDia.size();
        long salasUtilizadas = reservasDoDia.stream().map(ReservaDTO::sala).distinct().count();

        long tempoTotal = reservasDoDia.stream()
                .mapToLong(r -> Duration.between(r.inicio(), r.fim()).toMinutes())
                .sum();

        double media = (double) tempoTotal / countReservas;

        long maiorDuracao = reservasDoDia.stream()
                .mapToLong(r -> Duration.between(r.inicio(), r.fim()).toMinutes())
                .max()
                .orElse(0);

        return new EstatisticasDTO(countReservas, salasUtilizadas, tempoTotal, media, maiorDuracao);
    }
}