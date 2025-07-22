package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import lombok.Data;

@Data
public class ClimaDTO {
    private String descricao;
    private Double temperatura;
    private Double sensacaoTermica;
    private Double umidade;
    private Double velocidadeVento;
}
