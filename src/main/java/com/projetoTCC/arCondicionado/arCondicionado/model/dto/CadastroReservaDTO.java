package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroReservaDTO {
    private Long salaId;

    private Long matricula;

    private DayOfWeek diaSemana;

    private LocalTime horarioInicio;

    private LocalTime horarioFim;

    private boolean permanente =  false;
}
