package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SalaLocalDTO {
    private Long id;
    private String nome;
    private String localizacao;
    private String reservadoPor;
    private Long matricula;
    private Integer posicao;
    private Integer ligados;
    private Integer desligados;

}
