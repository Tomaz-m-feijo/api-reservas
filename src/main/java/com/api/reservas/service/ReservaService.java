package com.api.reservas.service;

import com.api.reservas.dto.ReservaDTO;
import com.api.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class ReservaService {

    private final ReservaRepository repository;
    private static final Set<String> SALAS_VALIDAS = Set.of(
            "A101", "A102", "A103", "B201", "B202", "B203", "C301", "C302"
    );

    public ReservaService(ReservaRepository repository) {
        this.repository = repository;
    }

    public synchronized void criarReserva(ReservaDTO reserva) {
        validarRegrasDeNegocio(reserva);
        repository.salvar(reserva);
    }

    private void validarRegrasDeNegocio(ReservaDTO dto) {
        if (!SALAS_VALIDAS.contains(dto.sala())) {
            throw new IllegalArgumentException("Sala inválida. Salas permitidas: " + SALAS_VALIDAS);
        }
        if (!dto.fim().isAfter(dto.inicio())) {
            throw new IllegalArgumentException("O horário de fim deve ser posterior ao horário de início.");
        }
        if (dto.inicio().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("A reserva não pode iniciar no passado.");
        }

        long minutos = Duration.between(dto.inicio(), dto.fim()).toMinutes();
        if (minutos < 30 || minutos > 120) {
            throw new IllegalArgumentException("A duração da reserva deve ser de no mínimo 30 minutos e no máximo 2 horas.");
        }
        if (dto.inicio().getMinute() % 30 != 0) {
            throw new IllegalArgumentException("O horário de início deve ocorrer em múltiplos de 30 minutos.");
        }

        LocalTime horaInicio = dto.inicio().toLocalTime();
        LocalTime horaFim = dto.fim().toLocalTime();
        LocalTime limiteInicio = LocalTime.of(8, 0);
        LocalTime limiteFim = LocalTime.of(18, 0);

        if (horaInicio.isBefore(limiteInicio) || horaFim.isAfter(limiteFim) ||
                (horaFim.equals(limiteFim) && dto.fim().getSecond() > 0)) {
            throw new IllegalArgumentException("A reserva deve ocorrer entre 08:00 e 18:00 do mesmo dia.");
        }

        if (!dto.inicio().toLocalDate().equals(dto.fim().toLocalDate())) {
            throw new IllegalArgumentException("A reserva deve iniciar e terminar no mesmo dia.");
        }

        boolean sobreposicao = repository.buscarTodas().stream()
                .filter(r -> r.sala().equals(dto.sala()))
                .anyMatch(r -> dto.inicio().isBefore(r.fim()) && dto.fim().isAfter(r.inicio()));

        if (sobreposicao) {
            throw new IllegalArgumentException("A sala já possui uma reserva com sobreposição de horário.");
        }
    }
}