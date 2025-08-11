package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaCreateDTO {
    @NotBlank
    private String nome;

    @NotBlank
    private String localizacao;

    @NotBlank
    private Integer posicao;
}