package com.api.reservas.repository;

import com.api.reservas.dto.ReservaDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class ReservaRepository {

    private final Map<LocalDate, List<ReservaDTO>> reservasPorData = new ConcurrentHashMap<>();

    public void salvar(ReservaDTO reserva) {
        LocalDate dataDaReserva = reserva.inicio().toLocalDate();

        reservasPorData.computeIfAbsent(dataDaReserva, k -> new CopyOnWriteArrayList<>()).add(reserva);
    }

    public List<ReservaDTO> buscarTodas() {
        return reservasPorData.values().stream()
                .flatMap(List::stream)
                .toList();
    }

    public List<ReservaDTO> buscarPorData(LocalDate data) {
        return reservasPorData.getOrDefault(data, List.of());
    }

    public void limparTodas() {
        reservasPorData.clear();
    }
}