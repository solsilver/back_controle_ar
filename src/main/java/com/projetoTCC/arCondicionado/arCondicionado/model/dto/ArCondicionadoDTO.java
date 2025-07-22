package com.projetoTCC.arCondicionado.arCondicionado.model.dto;


import lombok.Data;

@Data
public class ArCondicionadoDTO {
    private Long id;
    private String mensagem;
    private String acao;
    private int temperatura;
    private String modo;
    private String velocidade;
    private boolean swingAtivo;
    private String marca;

    public ArCondicionadoDTO(Long id, String mensagem, String acao, int temperatura,
                             String modo, String velocidade, boolean swingAtivo, String marca) {
        this.id = id;
        this.mensagem = mensagem;
        this.acao = acao;
        this.temperatura = temperatura;
        this.modo = modo;
        this.velocidade = velocidade;
        this.swingAtivo = swingAtivo;
        this.marca = marca;
    }
}