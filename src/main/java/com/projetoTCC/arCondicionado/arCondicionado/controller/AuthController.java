package com.projetoTCC.arCondicionado.arCondicionado.controller;


import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.LoginRequest;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.LoginResponse;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.UsuarioDTO;
import com.projetoTCC.arCondicionado.arCondicionado.service.UsuarioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        Usuario salvo = usuarioService.salvarUsuario(usuario);
        return ResponseEntity.ok(salvo);
    }
    @PostMapping("/cadastrar-excel")
    public ResponseEntity<byte[]> cadastrarPorExcel(@RequestParam("file") MultipartFile file) {
        try {
            byte[] excelBytes =  usuarioService.salvarUsuarioExcel(file);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "status_usuarios_cadastrados.xlsx");
            // Retorna a resposta com o arquivo e o status HTTP 200 OK.
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            // Em caso de erro, você pode retornar uma resposta com o status de erro
            // e uma mensagem. Por exemplo, 500 Internal Server Error.
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = usuarioService.login(request);
        return ResponseEntity.ok(loginResponse);
    }
    @GetMapping("/professores")
    public ResponseEntity<List<UsuarioDTO>> cadastrar() {
        List<UsuarioDTO> usuarioDTO = usuarioService.professores();
        return ResponseEntity.ok(usuarioDTO);
    }
}
