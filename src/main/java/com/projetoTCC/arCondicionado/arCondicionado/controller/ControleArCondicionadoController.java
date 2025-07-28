package com.projetoTCC.arCondicionado.arCondicionado.controller;

import com.projetoTCC.arCondicionado.arCondicionado.model.ConexaoESPDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.CadastroAparelhoDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.CadastroReservaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.ControleArCondicionadoUpdateDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.ReservaSalaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaCreateDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.service.ControleArCondicionadoService;
import com.projetoTCC.arCondicionado.arCondicionado.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/esp")
public class ControleArCondicionadoController {

    private final ControleArCondicionadoService service;
    private final SalaService salaService;

    public ControleArCondicionadoController(ControleArCondicionadoService service, SalaService salaService) {
        this.service = service;
        this.salaService = salaService;
    }

    @GetMapping("/conexao/{id}")
    public ConexaoESPDTO obterConexaoESP(@PathVariable Long id) {
        return service.buscarDadosConexaoPorId(id);
    }
    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarAparelho(@RequestBody CadastroAparelhoDTO dto) {
        ControleArCondicionado salvo = service.cadastrarAparelho(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("comandos/{id}")
    public ResponseEntity<?> buscarComando(@PathVariable Long id) {
        return service.buscarComandoParaEsp(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarControle(
            @PathVariable Long id,
            @RequestBody ControleArCondicionadoUpdateDTO dto) {
        return service.atualizarControle(id, dto);
    }
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> buscarTodos() {
        List<Map<String, Object>> resposta =service.buscarTodos();
        return ResponseEntity.ok(resposta);
    }
    @GetMapping("/arsalas")
    public ResponseEntity<List<Map<String, Object>>> buscarTodosArsSalas(@RequestParam Long salaId) {
        List<Map<String, Object>> resposta =salaService.buscarArsPorSala(salaId);
        return ResponseEntity.ok(resposta);
    }
    @GetMapping("/salas")
    public Page<SalaDTO> buscarSalas(
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return salaService.buscarSalas(nome, pageable);
    }

    @GetMapping("/salas/nomes")
    public List<String> buscarNomesSalas() {
        return salaService.buscarNomesSalas();
    }
    @PostMapping("/salas")
    public ResponseEntity<Sala> criarSala(@RequestBody @Valid SalaCreateDTO dto) {
        Sala novaSala = salaService.criarSala(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaSala);
    }
    @GetMapping("/salaReserva")
    public ResponseEntity<List<ReservaSalaDTO>> buscarTodosReservasSala(@RequestParam Long salaId) {
        List<ReservaSalaDTO> resposta =salaService.buscarReservasDaSala(salaId);
        return ResponseEntity.ok(resposta);
    }
    @PostMapping("/reservas")
    public ResponseEntity<Void> criarReservas(@RequestBody @Valid CadastroReservaDTO dto) {
         salaService.criarReserva(dto);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/reservas")
    public ResponseEntity<Void> deletarReservas(@RequestParam Long id) {
        salaService.deletarReserva(id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/ar")
    public ResponseEntity<Void> deletarAr(@RequestParam Long id) {
        service.deletarAr(id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/sala")
    public ResponseEntity<Void> excluirSala(@RequestParam Long id) {
        salaService.deletarSala(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/codigoesp", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<byte[]> baixarArquivoIno(@RequestParam Long id) {
        String conteudo = service.gerarCodigoArduino(id);

        byte[] conteudoBytes = conteudo.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("codigo_arduino_" + id + ".ino")
                .build());

        return new ResponseEntity<>(conteudoBytes, headers, HttpStatus.OK);
    }

}
