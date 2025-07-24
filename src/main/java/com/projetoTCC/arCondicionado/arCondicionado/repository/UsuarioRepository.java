package com.projetoTCC.arCondicionado.arCondicionado.repository;

import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByMatricula(Long matricula);
    @Query("SELECT u FROM Usuario u WHERE u.tipo = PROFESSOR")
    List<Usuario> buscarProfessores();
}
