package com.projetoTCC.arCondicionado.arCondicionado.service.Schedule;

import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.ReservaSala;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ControleArCondicionadoRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ReservaSalaRepository;
import com.projetoTCC.arCondicionado.arCondicionado.service.ControleArCondicionadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
public class LigarArAutomatico {
    @Autowired
    private ReservaSalaRepository reservaRepository;
    @Autowired
    private  ControleArCondicionadoRepository controleRepository;
    @Autowired
    private ControleArCondicionadoService controleArCondicionadoService;

    @Scheduled(cron = "0 */5 * * * *", zone = "America/Sao_Paulo")
    public void ligarArAntesDaReserva() {
        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek hoje = agora.getDayOfWeek();
        LocalTime cincoMinutosDepois = agora.plusMinutes(1).toLocalTime().truncatedTo(ChronoUnit.MINUTES);;

        List<ReservaSala> reservas = reservaRepository.findByDiaSemanaAndHorarioInicio(hoje, cincoMinutosDepois);

        for (ReservaSala reserva : reservas) {
            for (ControleArCondicionado ac : reserva.getSala().getAresCondicionados()) {
                if (!ac.isLigado()) {
                    ac.setLigado(true);
                    ac.setUltimaAtualizacao(LocalDateTime.now());
                    controleRepository.save(ac);
                    controleArCondicionadoService.enviarComando(ac);
                }
            }
        }
    }


}
