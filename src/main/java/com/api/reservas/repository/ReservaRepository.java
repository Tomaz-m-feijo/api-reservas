package com.api.reservas.repository;

import com.api.reservas.dto.ReservaDTO;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class ReservaRepository {
    private final List<ReservaDTO> reservas = new CopyOnWriteArrayList<>();

    public void salvar(ReservaDTO reserva) {
        reservas.add(reserva);
    }

    public List<ReservaDTO> buscarTodas() {
        return reservas;
    }

    public void limparTodas() {
        reservas.clear();
    }
}