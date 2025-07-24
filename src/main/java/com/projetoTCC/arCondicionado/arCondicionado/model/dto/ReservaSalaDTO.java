package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import com.projetoTCC.arCondicionado.arCondicionado.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaSalaDTO {

    private Long id;

    private Long salaId;

    private Long usuarioMatricula;

    private String usuarioNome;

    private DayOfWeek diaSemana;

    private LocalTime horarioInicio;

    private LocalTime horarioFim;

}
