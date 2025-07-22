package com.projetoTCC.arCondicionado.arCondicionado.service;

import com.projetoTCC.arCondicionado.arCondicionado.enums.TipoUsuarioEnum;
import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.ReservaSala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaCreateDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ControleArCondicionadoRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.ReservaSalaRepository;
import com.projetoTCC.arCondicionado.arCondicionado.repository.SalaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.projetoTCC.arCondicionado.arCondicionado.service.utils.UsuarioUtils.getUsuarioLogado;

@Service
public class SalaService {
    private final SalaRepository salaRepository;
    private final ControleArCondicionadoRepository arRepository;
    private final ReservaSalaRepository reservaSalaRepository;

    public SalaService(SalaRepository salaRepository, ControleArCondicionadoRepository arRepository, ReservaSalaRepository reservaSalaRepository) {
        this.salaRepository = salaRepository;
        this.arRepository = arRepository;
        this.reservaSalaRepository = reservaSalaRepository;
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


    public Page<SalaDTO> buscarSalas(String nomeFiltro, Pageable pageable) {
        Page<SalaDTO> salasPage = salaRepository.findByNomePage(nomeFiltro, pageable);
        LocalDateTime agora = LocalDateTime.now();
        DayOfWeek dia = agora.getDayOfWeek();
        LocalTime hora = agora.toLocalTime();
        salasPage.forEach(salaDTO -> {
            ReservaSala ativa = reservaSalaRepository.findAtiva(salaDTO.getId(), dia, hora);
            if (Objects.nonNull(ativa)) {
                salaDTO.setReservadoPor(ativa.getUsuario().getNome());
            }
        });
        return salasPage;
    }

    public List<String> buscarNomesSalas() {
        return salaRepository.findAll().stream()
                .map(Sala::getNome)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> buscarArsPorSala(Long salaId) {
        Sala salas = salaRepository.findById(salaId).orElseThrow( () -> new RuntimeException("Dispositivo não encontrado"));
        List<ControleArCondicionado> controles = salas.getAresCondicionados();
        return controles.stream().map(controle -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", controle.getId());
            item.put("mensagem", "Comando carregado com sucesso");
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

}
