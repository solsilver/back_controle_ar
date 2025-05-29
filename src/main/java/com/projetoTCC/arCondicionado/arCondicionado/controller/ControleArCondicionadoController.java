package com.projetoTCC.arCondicionado.arCondicionado.controller;

import com.projetoTCC.arCondicionado.arCondicionado.model.ConexaoESPDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.CadastroAparelhoDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.ControleArCondicionadoUpdateDTO;
import com.projetoTCC.arCondicionado.arCondicionado.service.ControleArCondicionadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/esp")
public class ControleArCondicionadoController {

    private final ControleArCondicionadoService service;

    public ControleArCondicionadoController(ControleArCondicionadoService service) {
        this.service = service;
    }

    @GetMapping("/conexao/{id}")
    public ConexaoESPDTO obterConexaoESP(@PathVariable Long id) {
        return service.buscarDadosConexaoPorId(id);
    }
    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarAparelho(@RequestBody CadastroAparelhoDTO dto) {
        ControleArCondicionado salvo = service.cadastrarAparelho(dto);
        String codigoArduino = service.gerarCodigoArduino(salvo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(codigoArduino);
    }
    @GetMapping("/{id}")
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


}
