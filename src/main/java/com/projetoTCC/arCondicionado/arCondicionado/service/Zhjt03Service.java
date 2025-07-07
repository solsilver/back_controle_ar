package com.projetoTCC.arCondicionado.arCondicionado.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class Zhjt03Service {

    public List<Integer> gerarRaw(int temperatura, String modo, String velocidade, String swing) {
        List<Integer> raw = new ArrayList<>();

        // Header
        raw.add(6234);
        raw.add(7392);

        // Monta a string com base nas definições do codes.h
        String hexCommand = 
              "FF00FF00" // HEADER
            + getModoHex(modo)
            + getTemperaturaHex(temperatura)
            + getVelocidadeHex(velocidade)
            + getSwingHex(swing)
            + "54AB";   // FOOTER

        byte[] payload = hexStringToByteArray(hexCommand);

        // Converte byte[] em raw signal (marca = 608, espaço = 608 ou 1740)
        for (byte b : payload) {
            for (int i = 7; i >= 0; i--) {
                raw.add(608); // pulso fixo
                raw.add(((b >> i) & 1) == 1 ? 1740 : 608); // pausa 1 ou 0
            }
        }

        // Footer
        raw.add(608);
        raw.add(7372);
        raw.add(616);

        return raw;
    }

    private String getModoHex(String modo) {
        return switch (modo.toUpperCase()) {
            case "COOL" -> "0B04";
            case "HEAT" -> "0E01";
            case "DRY"  -> "0D02";
            case "FAN"  -> "0906";
            case "AUTO" -> "0F00";
            default     -> "0B04"; // default: COOL
        };
    }

    private String getTemperaturaHex(int temp) {
        return switch (temp) {
            case 16 -> "F000";
            case 17 -> "7080";
            case 18 -> "B040";
            case 19 -> "30C0";
            case 20 -> "D020";
            case 21 -> "50A0";
            case 22 -> "9060";
            case 23 -> "10E0";
            case 24 -> "E010";
            case 25 -> "6090";
            case 26 -> "A050";
            case 27 -> "20D0";
            case 28 -> "C030";
            case 29 -> "40B0";
            case 30 -> "8070";
            case 31 -> "00F0";
            case 32 -> "F000"; // repete
            default -> "E010"; // default: 24
        };
    }

    private String getVelocidadeHex(String vel) {
        return switch (vel.toUpperCase()) {
            case "SLOW"   -> "0906";
            case "MEDIUM" -> "0D02";
            case "FAST"   -> "0B04";
            case "SMART"  -> "0F00";
            default       -> "0B04"; // default: FAST
        };
    }

    private String getSwingHex(String swing) {
        return switch (swing.toUpperCase()) {
            case "0" -> "A050";
            case "1" -> "B040";
            case "2" -> "9060";
            default  -> "A050"; // default: swing 0
        };
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] result = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                  + Character.digit(s.charAt(i+1), 16));
        }
        return result;
    }
}
