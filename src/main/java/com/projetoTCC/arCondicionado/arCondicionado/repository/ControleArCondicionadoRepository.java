package com.projetoTCC.arCondicionado.arCondicionado.repository;

import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ControleArCondicionadoRepository extends JpaRepository<ControleArCondicionado, Long> {
    ControleArCondicionado findTopByOrderByUltimaAtualizacaoDesc();
}
