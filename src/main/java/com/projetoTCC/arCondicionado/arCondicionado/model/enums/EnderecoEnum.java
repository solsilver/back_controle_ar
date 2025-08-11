package com.projetoTCC.arCondicionado.arCondicionado.model.enums;

public enum EnderecoEnum {
    BLOCO_0("Bloco 0"),
    BLOCO_1("Bloco 1"),
    S_401("S-401"),
    T_401("T-401"),
    S_501("S-501"),
    T_501("T-501"),
    S_301("S-301"),
    T_301("T-301"),
    ADMINISTRATIVO("Administrativo"),
    AUDITORIO("Auditório");

    private final String descricao;

    EnderecoEnum(String descricao) {
        this.descricao = descricao;
    }
    public static String getDescricaoPorNome(String nome) {
        try {
            return EnderecoEnum.valueOf(nome).getDescricao();
        } catch (IllegalArgumentException | NullPointerException e) {
            // Se não achar, pode retornar null ou alguma mensagem padrão
            return null;
            // ou: return "Descrição não encontrada";
        }
    }

    public String getDescricao() {
        return descricao;
    }
}