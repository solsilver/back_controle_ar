package com.projetoTCC.arCondicionado.arCondicionado.service;

import com.projetoTCC.arCondicionado.arCondicionado.enums.TipoUsuarioEnum;
import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.ReservaSala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.CadastroReservaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.ReservaSalaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaCreateDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaPosicaoDTO;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ControleArCondicionadoRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ReservaSalaRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.SalaRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.projetoTCC.arCondicionado.arCondicionado.service.utils.UsuarioUtils.getUsuarioLogado;

@Service
public class SalaService {
    private final SalaRepository salaRepository;
    private final ControleArCondicionadoRepository arRepository;
    private final ReservaSalaRepository reservaSalaRepository;
    private final UsuarioRepository usuarioRepository;

    public SalaService(SalaRepository salaRepository, ControleArCondicionadoRepository arRepository, ReservaSalaRepository reservaSalaRepository, UsuarioRepository usuarioRepository) {
        this.salaRepository = salaRepository;
        this.arRepository = arRepository;
        this.reservaSalaRepository = reservaSalaRepository;
        this.usuarioRepository = usuarioRepository;
    }
    public Sala criarSala(SalaCreateDTO dto) {
        Usuario usuario = getUsuarioLogado();
        boolean ehAdmin = usuario.getTipo() == TipoUsuarioEnum.ADMINISTRATIVO;
        if(!ehAdmin){
            throw new RuntimeException("Você não possui permissao para cadastrar uma nova sala");
        }
        Sala sala = Sala.builder()
                .nome(dto.getNome())
                .localizacao(dto.getLocalizacao())
                .build();

        return salaRepository.save(sala);
    }


    public List<SalaDTO> buscarSalas(Long local) {
        List<SalaDTO> salasPage = salaRepository.findByLocal(local);
        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek dia = agora.getDayOfWeek();
        LocalTime hora = agora.toLocalTime();
        salasPage.forEach(salaDTO -> {
            ReservaSala ativa = reservaSalaRepository.findAtiva(salaDTO.getId(), dia, hora);
            if (Objects.nonNull(ativa)) {
                salaDTO.setReservadoPor(ativa.getUsuario().getNome());
                salaDTO.setMatricula(ativa.getUsuario().getMatricula());
            }
        });
        return salasPage;
    }

    public List<String> buscarNomesSalas() {
        return salaRepository.findAll().stream()
                .map(Sala::getNome)
                .collect(Collectors.toList());
    }
    public void modificarPosicao(List<SalaPosicaoDTO> salaPosicaoDTO) {
        salaPosicaoDTO.forEach(s ->{
            Sala sala = salaRepository.findById(s.getId()).orElseThrow( () -> new RuntimeException("Sala não encontrado"));
            sala.setPosicao(s.getPosicao());
            sala.setLado(s.getLado());
            salaRepository.save(sala);
        });
    }

    public List<Map<String, Object>> buscarArsPorSala(Long salaId) {
        Sala salas = salaRepository.findById(salaId).orElseThrow( () -> new RuntimeException("Sala não encontrado"));
        List<ControleArCondicionado> controles = salas.getAresCondicionados();
        return controles.stream().map(controle -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", controle.getId());
            item.put("nome", controle.getNome());
            item.put("acao", controle.isLigado() ? "LIGAR" : "DESLIGAR");
            item.put("temperatura", controle.getTemperatura());
            item.put("modo", controle.getModo().name());
            item.put("velocidade", controle.getVelocidade().name());
            item.put("swingAtivo", controle.isSwingAtivo());
            item.put("marca",controle.getMarca());
            return item;
        }).collect(Collectors.toList());
    }
    public Sala salaByid(Long salaId){
        return  salaRepository.findById(salaId).orElseThrow( () -> new RuntimeException("Dispositivo não encontrado"));
    }

    public List<ReservaSalaDTO> buscarReservasDaSala(Long salaId){
        List<ReservaSala> reservasSala = reservaSalaRepository.findReservasSala(salaId);
        List<ReservaSalaDTO> reservaSalaDTOs = reservasSala.stream()
                .map(reserva -> new ReservaSalaDTO(
                        reserva.getId(),
                        reserva.getSala().getId(),
                        reserva.getUsuario().getMatricula(),
                        reserva.getUsuario().getNome(),
                        reserva.getDiaSemana(),
                        reserva.getHorarioInicio(),
                        reserva.getHorarioFim()
                ))
                .collect(Collectors.toList());
        return reservaSalaDTOs;
    }

    public void criarReserva(@Valid CadastroReservaDTO dto) {
        Usuario usuarioLogado = getUsuarioLogado();

        if (Objects.nonNull(dto.getMatricula())) {
             usuarioLogado = usuarioRepository.findByMatricula(dto.getMatricula())
                     .orElseThrow(()-> new RuntimeException("usuario nao encontrado."));
        }

        Sala sala = salaRepository
                .findById(dto.getSalaId()).orElseThrow(() -> new RuntimeException("Sala não encontrada"));
        ReservaSala reservaSala = ReservaSala.builder()
                .sala(sala)
                .horarioFim(dto.getHorarioFim())
                .horarioInicio(dto.getHorarioInicio())
                .usuario(usuarioLogado)
                .diaSemana(dto.getDiaSemana())
                .permanente(dto.isPermanente()).build();
        reservaSalaRepository.save(reservaSala);

    }
    @Transactional
    public void deletarReserva(Long id) {
        ReservaSala reserva = reservaSalaRepository.findById(id).orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        Sala salaReserva = reserva.getSala();
        salaReserva.getReservas().remove(reserva);
        reservaSalaRepository.delete(reserva);
    }
    @Transactional
    public void deletarSala(Long id) {
        salaRepository.deleteById(id);
    }
}
