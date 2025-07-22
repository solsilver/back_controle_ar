package com.projetoTCC.arCondicionado.arCondicionado.repository;

import com.projetoTCC.arCondicionado.arCondicionado.model.ReservaSala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaSalaRepository extends JpaRepository<ReservaSala, Long> {
    List<ReservaSala> findByDiaSemanaAndHorarioInicio(DayOfWeek diaSemana, LocalTime horarioInicio);
    List<ReservaSala> findByDiaSemanaAndHorarioFim(DayOfWeek diaSemana, LocalTime horarioFim);
    boolean existsBySalaAndDiaSemanaAndHorarioInicioAfter(Sala sala, DayOfWeek diaSemana, LocalTime horarioAtual);

    @Query("SELECT r FROM ReservaSala r WHERE r.sala.id = :salaId AND r.diaSemana = :diaSemana AND :hora BETWEEN r.horarioInicio AND r.horarioFim")
    Optional<ReservaSala> findAtiva(@Param("salaId") Long salaId, @Param("diaSemana") DayOfWeek diaSemana, @Param("hora") LocalTime hora);

    List<ReservaSala> findByDiaSemanaAndHorarioFimAndPermanenteFalse(DayOfWeek dia, LocalTime horarioFim);
}
