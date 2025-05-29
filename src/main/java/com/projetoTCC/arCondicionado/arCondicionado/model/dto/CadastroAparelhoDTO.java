package com.projetoTCC.arCondicionado.arCondicionado.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroAparelhoDTO {
    private String nome;
    private String ssid;
    private String senhaWifi;
}
