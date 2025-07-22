package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import com.projetoTCC.arCondicionado.arCondicionado.model.Sala;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SalaDTO {
    private Long id;
    private String nome;
    private String localizacao;
    private String reservadoPor;

    public SalaDTO(Sala sala) {
        this.id = sala.getId();
        this.nome = sala.getNome();
    }
}