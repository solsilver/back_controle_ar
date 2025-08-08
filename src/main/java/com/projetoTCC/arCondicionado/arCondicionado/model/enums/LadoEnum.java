package com.projetoTCC.arCondicionado.arCondicionado.model.enums;

public enum LadoEnum {
    DIREITA("dir"),
    ESQUERDA("esq");

    private final String sigla;

    LadoEnum(String sigla) {
        this.sigla = sigla;
    }

    public String getSigla() {
        return sigla;
    }
}
