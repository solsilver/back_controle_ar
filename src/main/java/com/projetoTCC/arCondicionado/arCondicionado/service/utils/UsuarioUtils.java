package com.projetoTCC.arCondicionado.arCondicionado.service.utils;

import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import com.projetoTCC.arCondicionado.arCondicionado.model.UsuarioDetails;
import com.projetoTCC.arCondicionado.arCondicionado.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UsuarioUtils {
    @Autowired
    private static UsuarioRepository usuarioRepository;
    public static Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UsuarioDetails usuarioDetails) {
            return usuarioDetails.getUsuario();
        }

        return null;
    }
    public static Usuario getUsuarioByMatricula(Long matricula){
        return usuarioRepository.findByMatricula(matricula).orElseThrow(()-> new RuntimeException("usuario nao encontrado."));
    }
}
