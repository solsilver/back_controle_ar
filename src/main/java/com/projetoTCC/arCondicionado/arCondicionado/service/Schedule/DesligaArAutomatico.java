package com.projetoTCC.arCondicionado.arCondicionado.service.Schedule;

import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.ReservaSala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ControleArCondicionadoRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ReservaSalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
public class DesligaArAutomatico {
    @Autowired
    private ReservaSalaRepository reservaRepository;
    @Autowired
    private ControleArCondicionadoRepository controleRepository;

    @Scheduled(cron = "0 */5 * * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void desligarArSemReserva() {
        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek hoje = agora.getDayOfWeek();
        LocalTime horaAtual = agora.toLocalTime().truncatedTo(ChronoUnit.MINUTES);

        List<ReservaSala> reservasQueTerminaram = reservaRepository.findByDiaSemanaAndHorarioFim(hoje, horaAtual);

        for (ReservaSala reserva : reservasQueTerminaram) {
            Sala sala = reserva.getSala();
            boolean haProximaReserva = reservaRepository.existsBySalaAndDiaSemanaAndHorarioInicio(
                    sala, hoje, horaAtual
            );

            if (!haProximaReserva) {
                for (ControleArCondicionado ac : sala.getAresCondicionados()) {
                    if (ac.isLigado()) {
                        ac.setLigado(false);
                        ac.setUltimaAtualizacao(LocalDateTime.now());
                        controleRepository.save(ac);
                    }
                }
            }
            if(!reserva.isPermanente()){
                Sala salaReserva = reserva.getSala();
                salaReserva.getReservas().remove(reserva);
                reservaRepository.delete(reserva);
            }
        }
    }

}
