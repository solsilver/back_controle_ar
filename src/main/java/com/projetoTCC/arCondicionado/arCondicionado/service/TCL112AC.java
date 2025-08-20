package com.projetoTCC.arCondicionado.arCondicionado.service;

public class TCL112AC {

    public enum Mode { COOL, HEAT }
    public enum Fan { AUTO, LOW, MEDIUM, HIGH }

    public static String gerarComando(int temperature, Mode mode, Fan fan) {
        if (temperature < 16 || temperature > 30) {
            throw new IllegalArgumentException("Temperatura deve ser entre 16 e 30°C");
        }

        byte[] state = new byte[14];

        // Prefixo fixo
        state[0] = 0x23;
        state[1] = (byte)0xCB;
        state[2] = 0x26;
        state[3] = 0x01;
        state[4] = 0x00;
        state[5] = 0x24;

        // Byte de temperatura (corrigido)
        int tempByte = 31 - temperature; // 16°C -> 0x0F, 24°C -> 0x07, 30°C -> 0x01
        state[6] = (byte)(mode == Mode.COOL ? 0x03 : 0x01); // COOL=3, HEAT=1
        state[7] = (byte)tempByte;

        // Ventilação
        int fanByte = switch (fan) {
            case AUTO -> 0x00;
            case LOW -> 0x02;
            case MEDIUM -> 0x03;
            case HIGH -> 0x05;
        };
        state[8] = (byte)fanByte;

        // Bytes fixos
        state[9] = 0x00;
        state[10] = 0x00;
        state[11] = 0x00;

        // Checksum
        state[12] = (byte)0x80; // sempre 0x80

        int baseChecksum;
        if (mode == Mode.HEAT) {
            baseChecksum = 0xC9 - (temperature - 16); // HEAT AUTO
        } else {
            baseChecksum = 0xCB - (temperature - 16); // COOL AUTO
        }

        // Offset da ventilação
        int fanOffset = switch (fan) {
            case AUTO -> 0;
            case LOW -> 2;
            case MEDIUM -> 3;
            case HIGH -> 5;
        };

        state[13] = (byte)(baseChecksum + fanOffset);

        // Monta string hexadecimal
        StringBuilder hex = new StringBuilder("0x");
        for (byte b : state) {
            hex.append(String.format("%02X", b & 0xFF));
        }
        return hex.toString();
    }

}
