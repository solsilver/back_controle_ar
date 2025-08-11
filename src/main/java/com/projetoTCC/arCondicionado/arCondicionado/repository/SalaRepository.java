package com.projetoTCC.arCondicionado.arCondicionado.repository;

import com.projetoTCC.arCondicionado.arCondicionado.model.ReservaSala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaDTO;
import com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaLocalDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaRepository extends JpaRepository<Sala, Long> {
    @Query("SELECT new com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaLocalDTO(s.id, s.nome, s.localizacao, null , null, s.posicao, null, null) " +
            "FROM Sala s WHERE (:localizacao IS NULL OR LOWER(s.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%')))")
    List<SalaLocalDTO> findByLocal(@Param("localizacao") String localizacao);
    @Query("SELECT new com.projetoTCC.arCondicionado.arCondicionado.model.dto.SalaDTO(s.id, s.nome, s.localizacao, null , null) " +
            "FROM Sala s WHERE (:nomeFiltro IS NULL OR LOWER(s.nome) LIKE LOWER(CONCAT('%', :nomeFiltro, '%')))")
    Page<SalaDTO> findByNomePage(@Param("nomeFiltro") String nomeFiltro, Pageable pageable);

}
