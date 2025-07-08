package com.projetoTCC.arCondicionado.arCondicionado.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class Zhjt03Service {

    private static final String PREFIX = "D5";
    private static final String CODE_OFF = "D56214000000";
    private static final String CODE_SWING = "D56216040000";

    private static final Map<String, Integer> modoBase = Map.of(
            "COOL", 0x20,
            "HEAT", 0x80,
            "FAN",  0x60,
            "DRY",  0x40,
            "AUTO", 0x00
    );

    private static final Map<String, Integer> velocidadeFanMap = Map.of(
            "AUTO",  0x1A,
            "BAIXO", 0x7A,
            "MEDIO", 0x5A,
            "ALTO",  0x3A
    );

    /**
     * Gera o(s) código(s) IR baseado em modo, temperatura, velocidade, swing e status ligado.
     *
     * @param modo         Modo de operação: COOL, HEAT, FAN, DRY, AUTO
     * @param temperatura  Temperatura entre 16 e 30
     * @param velocidade   Velocidade: AUTO, BAIXO, MEDIO, ALTO
     * @param swing        true = ativa swing (gera código extra)
     * @param ligado       true = ligado, false = desligado
     * @return Lista de strings com os códigos IR em hexadecimal
     */
    public List<String> gerarCodigoIr(String modo, int temperatura, String velocidade, boolean swing, boolean ligado) {
        List<String> codigos = new ArrayList<>();

        if (!ligado) {
            codigos.add("0x" + CODE_OFF);
            return codigos;
        }

        if (temperatura < 16 || temperatura > 30) {
            throw new IllegalArgumentException("Temperatura deve estar entre 16 e 30.");
        }

        Integer baseModo = modoBase.get(modo.toUpperCase());
        if (baseModo == null) {
            throw new IllegalArgumentException("Modo inválido. Use: COOL, HEAT, FAN, DRY, AUTO.");
        }

        Integer byteVelocidade = velocidadeFanMap.get(velocidade.toUpperCase());
        if (byteVelocidade == null) {
            throw new IllegalArgumentException("Velocidade inválida. Use: AUTO, BAIXO, MEDIO, ALTO.");
        }

        int byteModoTemp = baseModo + (temperatura - 16); // byte 2
        String codigo = String.format("0x%s%02X%02X050000", PREFIX, byteModoTemp, byteVelocidade);
        codigos.add(codigo);

        if (swing) {
            codigos.add("0x" + CODE_SWING);
        }

        return codigos;
    }
}
