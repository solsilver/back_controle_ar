package com.projetoTCC.arCondicionado.arCondicionado.service.Schedule;

import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.ReservaSala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ControleArCondicionadoRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ReservaSalaRepository;
import com.projetoTCC.arCondicionado.arCondicionado.service.ControleArCondicionadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
public class DesligaArAutomatico {
    @Autowired
    private ReservaSalaRepository reservaRepository;
    @Autowired
    private ControleArCondicionadoRepository controleRepository;
    @Autowired
    private ControleArCondicionadoService controleArCondicionadoService;

    private static final Set<LocalTime> HORARIOS_FIM_VALIDOS = Set.of(
            LocalTime.of(1, 0),
            LocalTime.of(2, 0),
            LocalTime.of(3, 0),
            LocalTime.of(4, 0),
            LocalTime.of(5, 0),
            LocalTime.of(6, 0),
            LocalTime.of(7, 30),
            LocalTime.of(8, 30),
            LocalTime.of(8, 45),
            LocalTime.of(10, 15),
            LocalTime.of(10, 30),
            LocalTime.of(12, 0),
            LocalTime.of(13, 0),
            LocalTime.of(14, 30),
            LocalTime.of(14, 45),
            LocalTime.of(16, 15),
            LocalTime.of(16, 30),
            LocalTime.of(18, 0),
            LocalTime.of(19, 0),
            LocalTime.of(20, 0),
            LocalTime.of(21, 0),
            LocalTime.of(22, 0),
            LocalTime.of(23, 0),
            LocalTime.MIDNIGHT
    );

    @Scheduled(cron = "0 */5 * * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void desligarAresSemReserva() {
        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek hoje = agora.getDayOfWeek();
        LocalTime horaAtual = agora.toLocalTime().truncatedTo(ChronoUnit.MINUTES);

        // Só processa se horaAtual for um horário de fim válido
        if (!HORARIOS_FIM_VALIDOS.contains(horaAtual)) {
            return;
        }

        // 1) Apagar reservas não permanentes que acabaram agora
        List<ReservaSala> reservasQueTerminaram = reservaRepository
                .findByDiaSemanaAndHorarioFim(hoje, horaAtual);

        for (ReservaSala reserva : reservasQueTerminaram) {
            if (!reserva.isPermanente()) {
                reservaRepository.delete(reserva);
            }
        }

        // 2) Desligar os ares se não houver próxima reserva
        List<Sala> salas = controleArCondicionadoService.salas();

        for (Sala sala : salas) {
            boolean haReservaFutura = reservaRepository
                    .existsBySalaAndDiaSemanaAndHorarioInicioAfter(sala, hoje, horaAtual);

            if (!haReservaFutura) {
                List<ControleArCondicionado> aresLigados = sala.getAresCondicionados();

                for (ControleArCondicionado ac : aresLigados) {
                    ac.setLigado(false);
                    ac.setUltimaAtualizacao(LocalDateTime.now());
                }

                if (!aresLigados.isEmpty()) {
                    controleRepository.saveAll(aresLigados);
                    aresLigados.forEach(controleArCondicionadoService::enviarComando);
                }
            }
        }
    }


}
