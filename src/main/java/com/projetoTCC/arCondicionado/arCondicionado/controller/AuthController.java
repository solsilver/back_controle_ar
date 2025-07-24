package com.projetoTCC.arCondicionado.arCondicionado.controller;

import com.projetoTCC.arCondicionado.arCondicionado.config.JwtService;
import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.LoginRequest;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.LoginResponse;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.ReservaSalaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.UsuarioDTO;
import com.projetoTCC.arCondicionado.arCondicionado.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder encoder,
                          AuthenticationManager authManager,
                          JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        Usuario salvo = usuarioRepository.save(usuario);
        return ResponseEntity.ok(salvo);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(String.valueOf(request.getMatricula()), request.getSenha()));

        Usuario usuario = usuarioRepository.findByMatricula(request.getMatricula())
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String roleName = "ROLE_" + usuario.getTipo().name();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

        String token = jwtService.generateToken(new User(
            String.valueOf(usuario.getMatricula()),
            "", authorities
        ));
        LoginResponse loginResponse = new LoginResponse(usuario.getId(), usuario.getNome(),
                usuario.getMatricula().toString(),token, usuario.getTipo());
        return ResponseEntity.ok(loginResponse);
    }
    @GetMapping("/professores")
    public ResponseEntity<List<UsuarioDTO>> cadastrar() {
        List<Usuario> professores = usuarioRepository.buscarProfessores();
        List<UsuarioDTO> usuarioDTO = professores.stream().map(s ->
            new UsuarioDTO(s.getMatricula(),s.getNome())
        ).collect(Collectors.toList());
        return ResponseEntity.ok(usuarioDTO);
    }
}
