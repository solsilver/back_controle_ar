package com.projetoTCC.arCondicionado.arCondicionado.repository;

import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaRepository extends JpaRepository<Sala, Long> {
    @Query("SELECT new com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaDTO(s.id, s.nome, s.localizacao, null , null, s.posicao, s.lado) " +
            "FROM Sala s WHERE s.local.id = :local")
    List<SalaDTO> findByLocal(@Param("local") Long local);


}
