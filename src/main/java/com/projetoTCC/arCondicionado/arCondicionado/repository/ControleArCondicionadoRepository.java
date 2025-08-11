package com.projetoTCC.arCondicionado.arCondicionado.repository;

import com.projetoTCC.arCondicionado.arCondicionado.model.ControleArCondicionado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ControleArCondicionadoRepository extends JpaRepository<ControleArCondicionado, Long> {
    ControleArCondicionado findTopByOrderByUltimaAtualizacaoDesc();

    @Modifying
    @Query(value = "DELETE FROM CONTROLE_AR_CONDICIONADO WHERE id = :idAr", nativeQuery = true)
    void deletarPorId(@Param("idAr") Long idAr);
    @Query("SELECT COUNT(c) FROM ControleArCondicionado c JOIN c.sala s WHERE (c.ligado = true and s.id = :id)")
    Integer buscarQuantidadeLigados(Long id);
    @Query("SELECT COUNT(c) FROM ControleArCondicionado c JOIN c.sala s WHERE (c.ligado = false and s.id = :id)")
    Integer buscarQuantidadeDesligados(Long id);
}
