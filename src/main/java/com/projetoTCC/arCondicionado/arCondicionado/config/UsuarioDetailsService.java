package com.projetoTCC.arCondicionado.arCondicionado.config;

import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import com.projetoTCC.arCondicionado.arCondicionado.model.UsuarioDetails;
import com.projetoTCC.arCondicionado.arCondicionado.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public UsuarioDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String matriculaStr) throws UsernameNotFoundException {
        Long matricula = Long.parseLong(matriculaStr);

        Usuario usuario = repo.findByMatricula(matricula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new UsuarioDetails(usuario); // Aqui é a correção principal
    }
}
